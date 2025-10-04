package com.example.util;

import com.example.dto.FinancialChartData;
import com.example.dto.FinancialChartDto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartUtil {

    public static FinancialChartDto getMonthlyChartData(List<FinancialChartData> chartDataList, LocalDate startDate, LocalDate endDate) {


        // 데이터를 Map으로 변환 (년월을 키로) - 성능 최적화
        Map<YearMonth, Long> dataMap = chartDataList.stream()
                .collect(Collectors.toMap(
                        financialChartData -> YearMonth.from(financialChartData.getDate()),
                        FinancialChartData::getAmount,
                        Long::sum, // 중복 키 처리 (같은 월에 여러 데이터가 있을 경우)
                        java.util.LinkedHashMap::new // 순서 보장
                ));

        // count도 Map으로 변환
        Map<YearMonth, Long> countMap = chartDataList.stream()
                .collect(Collectors.toMap(
                        financialChartData -> YearMonth.from(financialChartData.getDate()),
                        FinancialChartData::getCount,
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

        return FinancialChartDto.builder()
                .data(data)
                .dates(dates)
                .counts(counts)
                .build();
    }

    public static FinancialChartDto getDailyChartData(List<FinancialChartData> chartDataList, LocalDate startDate, LocalDate endDate) {

        // 데이터를 Map으로 변환 (날짜를 키로)
        Map<LocalDate, Long> dataMap = chartDataList.stream()
                .collect(Collectors.toMap(FinancialChartData::getDate, FinancialChartData::getAmount));

        // count도 Map으로 변환
        Map<LocalDate, Long> countMap = chartDataList.stream()
                .collect(Collectors.toMap(FinancialChartData::getDate, FinancialChartData::getCount));

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

        return FinancialChartDto.builder()
                .data(data)
                .dates(dates)
                .counts(counts)
                .build();
    }

}
