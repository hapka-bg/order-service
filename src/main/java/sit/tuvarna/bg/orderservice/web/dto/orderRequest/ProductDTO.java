package sit.tuvarna.bg.orderservice.web.dto.orderRequest;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductDTO {

    private UUID id;
    private String name;
    private int quantity;
    private double price;
    private ProductCustomizationsDTO customizations;
}
