package com.example.customerservice.service;

import com.example.customerservice.dto.CustomerCreateRequest;
import com.example.customerservice.dto.CustomerResponse;
import com.example.customerservice.dto.CustomerSearchCriteria;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.repository.CustomerRepository;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;



    // 고객 생성
    @Transactional
    public void createCustomer(CustomerCreateRequest request, Integer storeId) {
        // 전화번호 중복 확인
        validatePhoneUniqueness(request.getPhone(), storeId, null);

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .storeId(storeId)
                .build();
        customerRepository.save(customer);
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

    /*// 고객 정보 수정
    @Transactional
    public CustomerResponse updateCustomer(Integer id, CustomerCreateRequest request, Integer storeId) {
        Customer existingCustomer = customerRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.CUSTOMER_NOT_FOUND));

        // 전화번호 변경 시 중복 확인
        validatePhoneUniqueness(request.getPhone(), storeId, existingCustomer.getPhone());

        existingCustomer.setName(request.getName());
        existingCustomer.setPhone(request.getPhone());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return CustomerResponse.from(updatedCustomer);
    }*/

    // 고객 삭제
    @Transactional
    public void deleteCustomer(Integer id, Integer storeId) {
        Customer customer = customerRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.CUSTOMER_NOT_FOUND));
        customerRepository.delete(customer);
    }



    // 통합 검색
    public List<CustomerResponse> searchAllCustomers( Integer storeId) {
        
        List<Customer> customerPage = customerRepository.findByStoreId(storeId);

        return customerPage.stream().map(cus -> modelMapper.map(cus, CustomerResponse.class)).toList();

    }

    // 고객 수 조회
    public long getCustomerCountByStoreId(Integer storeId) {
        return customerRepository.countByStoreId(storeId);
    }

    // 전화번호 중복 검증 (private 메서드)
    private void validatePhoneUniqueness(String newPhone, Integer storeId, String existingPhone) {
        if (newPhone != null && !newPhone.isEmpty()) {
            // 수정 시에는 기존 전화번호와 다를 때만 검증
            if (!newPhone.equals(existingPhone)) {
                if (customerRepository.existsByPhoneAndStoreId(newPhone, storeId)) {
                    throw new CommonException(CommonExceptionCode.DUPLICATE_LOGIN_ID);
                }
            }
        }
    }
}
