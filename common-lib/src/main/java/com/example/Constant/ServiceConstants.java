package com.example.Constant;

/**
 * 마이크로서비스 간 통신에 사용되는 공통 상수들
 */
public final class ServiceConstants {
    
    private ServiceConstants() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }
    
    // ===== 서비스명 상수 =====
    public static final String AUTH_SERVICE = "Auth Service";
    public static final String CUSTOMER_SERVICE = "Customer Service";
    public static final String VISIT_SERVICE = "Visit Service";
    public static final String BENEFIT_SERVICE = "Benefit Service";
    public static final String EXPENSE_SERVICE = "Expense Service";
    public static final String SALES_SERVICE = "Sales Service";
    public static final String STORE_SERVICE = "Store Service";

    public static final String INTERNAL_SERVICE_ERROR_CODE = "INTERNAL_SERVICE_ERROR";

}
