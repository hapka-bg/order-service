package sit.tuvarna.bg.orderservice.web.dto.onlineOrders;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OngoingOrdersDisplay {

    private UUID orderId;
    private UUID userId;
    private LocalDateTime timePlaced;
    private BigDecimal total;
    private String status;

    private String name;
}
