package com.example.salesservice.service;

import com.example.salesservice.dto.ServiceHistoryDto;
import com.example.salesservice.dto.ServiceItemDto;
import com.example.salesservice.entity.Visit;
import com.example.salesservice.repository.PaymentRepository;
import com.example.salesservice.repository.ServiceItemRepository;
import com.example.salesservice.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final PaymentRepository paymentRepository;
    private final VisitRepository visitRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final ModelMapper modelMapper;

    public List<ServiceHistoryDto> getCustomerServiceHistory(Integer customerId, Integer storeId) {
        return visitRepository.findByStoreIdAndCustomerId(storeId, customerId)
                .stream()
                .map(v -> {
                    List<ServiceItemDto> serviceItemDtos = v.getServiceItems()
                            .stream()
                            .map(si -> modelMapper.map(si, ServiceItemDto.class))
                            .toList();

                    return ServiceHistoryDto.builder()
                            .historyId(v.getId())
                            .date(v.getVisitDate())
                            .subtotalAmount(v.getTotalServiceAmount())
                            .discountAmount(v.getTotalServiceAmount() - v.getFinalServiceAmount())
                            .finalAmount(v.getFinalServiceAmount())
                            .memo(v.getMemo())
                            .services(serviceItemDtos)
                            .build();
                })
                .toList();
    }
}
