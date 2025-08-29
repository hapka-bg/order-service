package sit.tuvarna.bg.orderservice.web.dto.orderRequest;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductCustomizationDTO {
    private UUID id;
    private String name;
    private Integer quantity;
}
