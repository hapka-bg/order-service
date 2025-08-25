package sit.tuvarna.bg.orderservice.web.dto.analytics;

import lombok.Getter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;


@Getter
public class OrdersPerDay {
    private LocalDate orderDay;
    private Long orderCount;

    public OrdersPerDay(Date orderDay, Long orderCount) {
        this.orderDay = orderDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.orderCount = orderCount;
    }

    public OrdersPerDay(LocalDate orderDay, Long orderCount) {
        this.orderDay = orderDay;
        this.orderCount = orderCount;
    }
}
