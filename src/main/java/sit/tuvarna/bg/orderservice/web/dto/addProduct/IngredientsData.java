package sit.tuvarna.bg.orderservice.web.dto.addProduct;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class IngredientsData {
    private UUID id;
    private String name;
}
