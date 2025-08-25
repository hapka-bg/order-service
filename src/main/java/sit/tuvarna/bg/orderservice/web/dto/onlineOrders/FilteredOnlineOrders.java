package sit.tuvarna.bg.orderservice.web.dto.onlineOrders;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FilteredOnlineOrders {

    private UUID orderId;
    private String name;
    private LocalDateTime deliveredAt;
    private BigDecimal total;
    private String status;
}
