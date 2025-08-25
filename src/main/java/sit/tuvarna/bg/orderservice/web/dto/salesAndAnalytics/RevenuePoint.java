package sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics;


import java.math.BigDecimal;

public record RevenuePoint(String period, BigDecimal value) {
}
