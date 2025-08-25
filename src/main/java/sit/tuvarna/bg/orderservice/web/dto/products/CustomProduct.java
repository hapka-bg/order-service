package sit.tuvarna.bg.orderservice.web.dto.products;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class CustomProduct {
    private UUID id;
    private String name;
    private String imageURL;
    private BigDecimal price;
    private String description;
}
