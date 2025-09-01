package sit.tuvarna.bg.orderservice.web.dto.onlineOrders;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OnlineOrdersResponseForUser {

    private UUID orderId;
    private LocalDateTime createdAt;
    private BigDecimal total;
    private String status;
    private int itemsCount;

}
