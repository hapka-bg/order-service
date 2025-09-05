package sit.tuvarna.bg.orderservice.web.dto.orderRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductDTO {
    @NotNull(message = "Product ID is required.")
    private UUID id;
    @NotBlank(message = "Product name cannot be blank.")
    private String name;
    @Min(value = 1, message = "Product quantity must be at least 1.")
    private int quantity;
    @Positive(message = "Product price must be greater than 0.")
    private double price;

    private ProductCustomizationsDTO customizations;
}
