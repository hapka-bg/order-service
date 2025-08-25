package sit.tuvarna.bg.orderservice.web.dto.orders;

import lombok.Builder;
import lombok.Data;
import sit.tuvarna.bg.orderservice.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderSummary {
    private UUID orderId;
    private UUID userId;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private OrderStatus orderStatus;

    private String name;

    public OrderSummary(UUID orderId, UUID userId, LocalDateTime orderDate, BigDecimal total, OrderStatus orderStatus, String name) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.total = total;
        this.orderStatus = orderStatus;
        this.name = name;
    }
}
