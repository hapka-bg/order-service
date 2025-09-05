package sit.tuvarna.bg.orderservice.web.dto.users;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductsDto {

    private UUID id;
    private String category;
    private String name;
    private BigDecimal price;
    private Integer weight;
    private String image;

}
