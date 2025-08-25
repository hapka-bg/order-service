package sit.tuvarna.bg.orderservice.web.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class WaiterOrders {
    private UUID waiterId;
    private Long ordersCount;
    @Setter
    private String name="";
}
