package sit.tuvarna.bg.orderservice.web.dto.addProduct;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class IngredientDto {

    private UUID id;
    private boolean permanent;
    private boolean canBeAdded;
    private boolean canBeRemoved;
    @DecimalMin(value = "0.0", message = "Extra cost must be non-negative")
    private BigDecimal extraCost;
    @Min(value = 0, message = "Max quantity cannot be negative")
    private int maxQty;
}
