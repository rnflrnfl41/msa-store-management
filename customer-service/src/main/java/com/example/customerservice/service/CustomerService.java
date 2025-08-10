package com.example.customerservice.service;

import com.example.customerservice.dto.CustomerCreateRequest;
import com.example.customerservice.dto.CustomerResponse;
import com.example.customerservice.dto.CustomerSearchCriteria;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.repository.CustomerRepository;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;



    // 고객 생성
    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request, Integer storeId) {
        // 전화번호 중복 확인
        validatePhoneUniqueness(request.getPhone(), storeId, null);

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .storeId(storeId)
                .build();
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerResponse.from(savedCustomer);
    }

    // ID로 고객 조회 (store_id 검증 포함)
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
    }

    // 고객 정보 수정
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
    }

    // 고객 삭제
    @Transactional
    public void deleteCustomer(Integer id, Integer storeId) {
        Customer customer = customerRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.CUSTOMER_NOT_FOUND));
        customerRepository.delete(customer);
    }



    // 통합 검색
    public Page<CustomerResponse> searchCustomers(CustomerSearchCriteria criteria, Integer storeId) {
        // 정렬 설정
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(criteria.getSortOrder()) ? 
            Sort.Direction.DESC : Sort.Direction.ASC, 
            criteria.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        
        Page<Customer> customerPage = customerRepository.findByStoreIdAndSearchCriteria(
            storeId,
            criteria.getKeyword(),
            criteria.getName(),
            criteria.getPhone(),
            criteria.getStartDate(),
            criteria.getEndDate(),
            pageable
        );
        
        return customerPage.map(CustomerResponse::from);
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
