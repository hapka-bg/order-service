package sit.tuvarna.bg.orderservice.web.dto.onlineOrders;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDetailsForOnlineOrders {
    private String phoneNumber;
    private String address;
}
