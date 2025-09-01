package sit.tuvarna.bg.orderservice.web.dto.users;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ProductToDisplay {

    private UUID id;
    private String name;
    private BigDecimal price;
    private Integer weight;
    private String image;
    private String description;
    private String category;
    private String recipe;
    private List<CustomizationDto> customizations;
    private List<RecommendedProductDto> recommended;
}
