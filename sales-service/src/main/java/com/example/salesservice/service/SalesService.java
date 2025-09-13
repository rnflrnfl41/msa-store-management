package com.example.salesservice.service;

import com.example.salesservice.dto.SalesRegistrationDto;
import com.example.salesservice.dto.ServiceHistoryDto;
import com.example.salesservice.dto.ServiceItemDto;
import com.example.salesservice.entity.Payment;
import com.example.salesservice.entity.ServiceItem;
import com.example.salesservice.entity.Visit;
import com.example.salesservice.repository.PaymentRepository;
import com.example.salesservice.repository.ServiceItemRepository;
import com.example.salesservice.repository.VisitRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void registerSales(SalesRegistrationDto registrationDto, Integer storeId) {

        Visit visit = modelMapper.map(registrationDto, Visit.class);
        visit.setStoreId(storeId);

        visitRepository.save(visit);

        paymentRepository.save(Payment.builder()
                .amount(registrationDto.getFinalServiceAmount())
                .discount(registrationDto.getDiscountAmount())
                .paymentMethod(registrationDto.getPaymentMethod())
                .pointsUsed(registrationDto.getUsedPoint())
                .visit(visit)
                .build()
        );

        registrationDto.getServiceList().stream().map(s -> ServiceItem.builder()
                .serviceName(s.getName())
                .price(s.getPrice())
                .visit(visit)
                .build()
        ).forEach(serviceItemRepository::save);


    }
}
