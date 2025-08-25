package sit.tuvarna.bg.orderservice.web.dto.onlineOrders;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class OngoingOrderDetails {
    private Map<String,Integer> map;
    private String phoneNumber;
    private String address;

}
