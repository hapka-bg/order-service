package sit.tuvarna.bg.orderservice.web.dto.users;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CustomizationDto {
    private UUID id;
    private CustomizableIngredients ingredient;
    private boolean addable;
    private boolean removable;
    private BigDecimal extraCost;
    private Integer maxQuantity;
}
