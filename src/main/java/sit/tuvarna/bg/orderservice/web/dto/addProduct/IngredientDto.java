package sit.tuvarna.bg.orderservice.web.dto.addProduct;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class IngredientDto {

    private UUID id;
    private boolean permanent;
    private boolean canBeAdded;
    private boolean canBeRemoved;
    private BigDecimal extraCost;
    private int maxQty;
}
