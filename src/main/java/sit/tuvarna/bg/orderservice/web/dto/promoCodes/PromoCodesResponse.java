package sit.tuvarna.bg.orderservice.web.dto.promoCodes;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PromoCodesResponse {

    private String code;
    private BigDecimal discount;
    private BigDecimal minPurchase;
    private LocalDateTime deadline;

}
