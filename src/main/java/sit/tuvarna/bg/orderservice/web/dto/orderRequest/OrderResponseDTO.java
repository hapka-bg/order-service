package sit.tuvarna.bg.orderservice.web.dto.orderRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderResponseDTO {

    private UUID id;
    private OnlineOrderStatus onlineOrderStatus;
    private BigDecimal finalTotal;

}
