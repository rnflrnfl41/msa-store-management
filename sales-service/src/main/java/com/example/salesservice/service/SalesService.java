package com.example.salesservice.service;

import com.example.dto.BenefitUseRequest;
import com.example.exception.CommonException;
import com.example.exception.CommonExceptionCode;
import com.example.salesservice.dto.*;
import com.example.salesservice.entity.ErrorLog;
import com.example.salesservice.entity.Payment;
import com.example.salesservice.entity.Visit;
import com.example.salesservice.repository.ErrorLogRepository;
import com.example.salesservice.repository.PaymentRepository;
import com.example.salesservice.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.Constant.BenefitConstant.BENEFIT_USE_COMPLETE;
import static com.example.Constant.ServiceConstants.INTERNAL_SERVICE_ERROR_CODE;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesService {

    private final PaymentRepository paymentRepository;
    private final VisitRepository visitRepository;
    private final ModelMapper modelMapper;
    private final BenefitServiceClient benefitServiceClient;
    private final ErrorLogRepository errorLogRepository;
    private final RegisterSalesService registerSalesService;

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

    public void registerSales(SalesRegistrationDto registrationDto, Integer storeId) {

        //benefit-service에 보낼 parameter
        BenefitUseRequest useRequest = BenefitUseRequest.builder()
                .usedPoint(registrationDto.getUsedPoint())
                .usedCouponId(registrationDto.getUsedCouponId())
                .customerId(registrationDto.getCustomerId())
                .build();

        boolean benefitUsed = false;

        try{

            //benefit-service에 api 보내는 작업
            String response = benefitServiceClient.usePointCoupon(useRequest,storeId);

            if(!response.equals(BENEFIT_USE_COMPLETE)){
                throw new CommonException(CommonExceptionCode.BENEFIT_USE_FAILED);
            }

            benefitUsed = true;

            //별도 트랜잭션을 사용 해야하는대 같은 클래스 내의 메서드를 참조할때는 @Transcational이 안되서 별도 서비스로 분리
            registerSalesService.saveSalesData(registrationDto,storeId);

        }catch (Exception e){

            if (benefitUsed) {
                rollbackBenefitUsage(useRequest, storeId, "매출 등록");
            }

            throw e;

        }

    }


    public SalesSummaryResponse summarySales(String dateStr, Integer storeId) {

        LocalDate localDate = LocalDate.parse(dateStr);
        int month = localDate.getMonthValue();

        SalesSummary todaySummary = visitRepository.getSummarySalesDate(localDate,storeId);
        SalesSummary monthSummary = visitRepository.getSummarySalesMonth(month,storeId);

        return SalesSummaryResponse.builder()
                .today(todaySummary)
                .month(monthSummary)
                .build();

    }

    public SalesChartResponse getChartData(String type, LocalDate startDate, LocalDate endDate, Integer storeId) {

        if ("monthly".equals(type)) {
            return getMonthlyChartData(startDate, endDate, storeId);
        } else if ("daily".equals(type)) {
            return getDailyChartData(startDate, endDate, storeId);
        } else {
            throw new IllegalArgumentException("Invalid chart type: " + type);
        }
    }

    private SalesChartResponse getMonthlyChartData(LocalDate startDate, LocalDate endDate, Integer storeId) {
        List<ChartData> chartDataList = visitRepository.getMonthlyChartDataByPeriod(startDate, endDate, storeId);

        // 데이터를 Map으로 변환 (년월을 키로) - 성능 최적화
        Map<YearMonth, Long> dataMap = chartDataList.stream()
                .collect(Collectors.toMap(
                    chartData -> YearMonth.from(chartData.getDate()),
                    ChartData::getAmount,
                    Long::sum, // 중복 키 처리 (같은 월에 여러 데이터가 있을 경우)
                    java.util.LinkedHashMap::new // 순서 보장
                ));

        // count도 Map으로 변환
        Map<YearMonth, Long> countMap = chartDataList.stream()
                .collect(Collectors.toMap(
                    chartData -> YearMonth.from(chartData.getDate()),
                    ChartData::getCount,
                    Long::sum, // 중복 키 처리
                    java.util.LinkedHashMap::new // 순서 보장
                ));

        List<Long> data = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        // 월별로 모든 날짜를 생성하고 빈 날짜는 0으로 채움
        YearMonth current = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        while (!current.isAfter(end)) {
            LocalDate firstDayOfMonth = current.atDay(1);
            dates.add(firstDayOfMonth);
            data.add(dataMap.getOrDefault(current, 0L));
            counts.add(countMap.getOrDefault(current, 0L));
            current = current.plusMonths(1);
        }

        return SalesChartResponse.builder()
                .data(data)
                .dates(dates)
                .counts(counts)
                .build();
    }

    private SalesChartResponse getDailyChartData(LocalDate startDate, LocalDate endDate, Integer storeId) {
        List<ChartData> chartDataList = visitRepository.getDailyChartDataByPeriod(startDate, endDate, storeId);

        // 데이터를 Map으로 변환 (날짜를 키로)
        Map<LocalDate, Long> dataMap = chartDataList.stream()
                .collect(Collectors.toMap(ChartData::getDate, ChartData::getAmount));

        // count도 Map으로 변환
        Map<LocalDate, Long> countMap = chartDataList.stream()
                .collect(Collectors.toMap(ChartData::getDate, ChartData::getCount));

        List<Long> data = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        // 일별로 모든 날짜를 생성하고 빈 날짜는 0으로 채움
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            dates.add(current);
            data.add(dataMap.getOrDefault(current, 0L));
            counts.add(countMap.getOrDefault(current, 0L));
            current = current.plusDays(1);
        }

        return SalesChartResponse.builder()
                .data(data)
                .dates(dates)
                .counts(counts)
                .build();
    }

    public SalesDataResponse getSalesList(LocalDate date, int page, int limit, Integer storeId) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("visitTime").descending());
        Page<Visit> visitPage = visitRepository.findByVisitDateAndStoreId(date, storeId, pageable);

        List<SalesDataDto> sales = visitPage.getContent()
                .stream()
                .map(this::convertToSalesDataDto)
                .toList();

        return SalesDataResponse.builder()
                .sales(sales)
                .pagination(createPagination(page, visitPage))
                .build();
    }

    private SalesDataDto convertToSalesDataDto(Visit visit) {
        Payment payment = paymentRepository.findByVisit(visit)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.NO_VISIT_ID));

        return SalesDataDto.builder()
                .id(visit.getId())
                .originalAmount(visit.getTotalServiceAmount())
                .finalAmount(visit.getFinalServiceAmount())
                .discountAmount(visit.getTotalServiceAmount() - visit.getFinalServiceAmount())
                .memo(visit.getMemo())
                .date(visit.getVisitDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .time(visit.getVisitTime().format(DateTimeFormatter.ISO_LOCAL_TIME))
                .paymentMethod(payment.getPaymentMethod())
                .customerName(visit.getCustomerName())
                .usedCoupon(createUsedCouponDto(payment))
                .usedPoints(payment.getPointsUsed())
                .build();
    }

    private UsedCouponDto createUsedCouponDto(Payment payment) {
        int couponDiscountAmount = payment.getDiscount() - payment.getPointsUsed();

        return UsedCouponDto.builder()
                .id(payment.getUsedCouponId())
                .name(payment.getUsedCouponName())
                .discountAmount(couponDiscountAmount)
                .build();
    }

    private Pagination createPagination(int page, Page<Visit> visitPage) {
        return Pagination.builder()
                .page(page)
                .total((int) visitPage.getTotalElements())
                .totalPages(visitPage.getTotalPages())
                .build();
    }

    /**
     * 혜택 사용 롤백을 수행하는 공통 메서드
     */
    private void rollbackBenefitUsage(BenefitUseRequest useRequest, Integer storeId, String context) {
        try {
            String rollbackResponse = benefitServiceClient.usePointCouponRollback(useRequest, storeId);
            log.info("혜택 사용 롤백 완료 ({}): {}", context, rollbackResponse);
        } catch (Exception rollbackException) {
            log.error("🚨 혜택 사용 롤백 실패 ({}) - 데이터 불일치 발생!", context, rollbackException);
            log.error("고객 ID: {}, 상점 ID: {}, 사용된 포인트: {}, 사용된 쿠폰: {}",
                    useRequest.getCustomerId(), storeId,
                    useRequest.getUsedPoint(), useRequest.getUsedCouponId());

            errorLogRepository.save(ErrorLog.builder()
                    .uri("benefit-service usePointCouponRollback fail")
                    .code(INTERNAL_SERVICE_ERROR_CODE)
                    .errorId(UUID.randomUUID().toString())
                    .message(rollbackException.getMessage())
                    .status(500)
                    .stackTrace(ExceptionUtils.getStackTrace(rollbackException))
                    .build()
            );
        }
    }

    public void deleteSales(int visitId, Integer storeId) {
        Visit visit = visitRepository.findByIdAndStoreId(visitId, storeId)
                .orElseThrow(() -> new CommonException(CommonExceptionCode.NO_VISIT_ID));

        //사용된 포인트나 쿠폰이 있을 시
        if(visit.getTotalServiceAmount() != visit.getFinalServiceAmount()){
            Payment payment = paymentRepository.findByVisit(visit)
                    .orElseThrow(() -> new CommonException(CommonExceptionCode.NO_PAYMENT));

            BenefitUseRequest useRequest = BenefitUseRequest.builder()
                    .usedPoint(payment.getPointsUsed())
                    .usedCouponId(payment.getUsedCouponId() == null ? "" : payment.getUsedCouponId().toString())
                    .customerId(visit.getCustomerId())
                    .build();

            rollbackBenefitUsage(useRequest, storeId, "매출 삭제");
        }

        visitRepository.delete(visit);
    }
}
