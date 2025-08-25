package sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SumCount {
    private final BigDecimal sum;
    private final Long count;
}
