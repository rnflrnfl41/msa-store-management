package com.example.customerservice.service;

import com.example.customerservice.dto.CustomerBenefitResponse;
import com.example.dto.BenefitServiceBenefitResponse;
import com.example.customerservice.dto.CustomerCreateRequest;
import com.example.customerservice.dto.CustomerResponse;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.repository.CustomerRepository;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final BenefitServiceClient benefitServiceClient;



    // 고객 생성
    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request, Integer storeId) {
        // 전화번호 중복 확인
        validatePhoneUniqueness(request.getPhone(), storeId);

        Customer savedCustomer = customerRepository.save(
                Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .storeId(storeId)
                .build()
        );

        return modelMapper.map(savedCustomer, CustomerResponse.class);
    }

    /*// ID로 고객 조회 (store_id 검증 포함)
    public CustomerResponse getCustomerByIdAndStoreId(Integer id, Integer storeId) {
        Customer customer = customerRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.CUSTOMER_NOT_FOUND));
        return CustomerResponse.from(customer);
    }

    // public_id로 고객 조회 (store_id 검증 포함)
    public CustomerResponse getCustomerByPublicIdAndStoreId(String publicId, Integer storeId) {
        Customer customer = customerRepository.findByPublicIdAndStoreId(publicId, storeId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.CUSTOMER_NOT_FOUND));
        return CustomerResponse.from(customer);
    }*/

    // 고객 정보 수정
    @Transactional
    public void updateCustomer(Integer customerId, CustomerCreateRequest request, Integer storeId) {

        Customer existingCustomer = customerRepository.findByIdAndStoreId(customerId, storeId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.CUSTOMER_NOT_FOUND));

        // 전화번호 변경 시 중복 확인
        if(!request.getPhone().equals(existingCustomer.getPhone())){
            validatePhoneUniqueness(request.getPhone(), storeId);
        }

        existingCustomer.setName(request.getName());
        existingCustomer.setPhone(request.getPhone());

        customerRepository.save(existingCustomer);
    }

    // 고객 삭제
    @Transactional
    public void deleteCustomer(Integer id, Integer storeId) {
        Customer customer = customerRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.CUSTOMER_NOT_FOUND));
        customerRepository.delete(customer);
    }



    // 통합 검색
    public List<CustomerResponse> searchAllCustomers( Integer storeId) {
        
        List<Customer> customers = customerRepository.findByStoreId(storeId);

        return customers.stream().map(cus -> modelMapper.map(cus, CustomerResponse.class)).toList();

    }

    // 고객 수 조회
    public long getCustomerCountByStoreId(Integer storeId) {
        return customerRepository.countByStoreId(storeId);
    }

    private void validatePhoneUniqueness(String newPhone, Integer storeId) {
        if (customerRepository.existsByPhoneAndStoreId(newPhone, storeId)) {
            throw new CommonException(CommonExceptionCode.DUPLICATE_PHONE_NUM);
        }
    }

    public List<CustomerBenefitResponse> searchCustomersHaveBenefit(Integer storeId) {

        List<Customer> customers = customerRepository.findByStoreId(storeId);

        List<Integer> customerIds = customers.stream().map(Customer::getId).toList();

        //benefit-service에서 모든 customer 쿠폰이랑 point 가져오기
        List<BenefitServiceBenefitResponse> benefitList = benefitServiceClient.getCustomerBenefitListBatch(customerIds, storeId);

        // point-service에서 받아온 데이터 Map으로 변환 (빠른 조회를 위해)
        Map<Integer, BenefitServiceBenefitResponse> benefitMap = benefitList.stream()
            .collect(Collectors.toMap(
                BenefitServiceBenefitResponse::getCustomerId,
                Function.identity()
            ));

        // 고객 정보 + 혜택 정보 병합
        return customers.stream()
            .map(customer -> {
                BenefitServiceBenefitResponse benefit = benefitMap.get(customer.getId());
                CustomerBenefitResponse response = new CustomerBenefitResponse();
                response.setId(customer.getId());
                response.setName(customer.getName());
                response.setPhone(customer.getPhone());
                response.setPoints(benefit != null ? benefit.getPoints() : 0);
                response.setCoupons(benefit != null ? benefit.getCoupons() : null);
                return response;
            })
            .collect(Collectors.toList());

    }
}
