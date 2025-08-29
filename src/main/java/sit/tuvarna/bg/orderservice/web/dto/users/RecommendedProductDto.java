package sit.tuvarna.bg.orderservice.web.dto.users;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RecommendedProductDto {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String image;
}
