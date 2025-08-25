package sit.tuvarna.bg.orderservice.web.dto.onlineOrders;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrdersPerDayDTO {
    private LocalDate orderDate;
    private Long totalOrders;

    public OrdersPerDayDTO(LocalDate orderDay, Long orderCount) {
        this.orderDate = orderDay;
        this.totalOrders = orderCount;
    }
}
