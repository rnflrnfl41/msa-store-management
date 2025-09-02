package com.example.salesservice.controller;

import com.example.dto.ApiResponse;
import com.example.salesservice.dto.ServiceHistoryDto;
import com.example.salesservice.service.SalesService;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.Constant.HttpHeaderConstants.X_USER_STORE_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<List<ServiceHistoryDto>>> getCustomerServiceHitory(
            @PathVariable Integer customerId,
            @RequestHeader(X_USER_STORE_ID) String storeIdHeader
    ) {
        Integer storeId = Integer.parseInt(storeIdHeader);
        List<ServiceHistoryDto> historyDtoList = salesService.getCustomerServiceHistory(customerId, storeId);
        return ResponseUtil.success(historyDtoList);
    }

}
