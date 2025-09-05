package sit.tuvarna.bg.orderservice.web.dto.addProduct;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductDto {

    @NotBlank(message = "Product name is required")
    private String name;
    @NotBlank(message = "Product description is required")
    private String description;
    @Positive(message = "Price must be greater than 0")
    private double price;
    @Positive(message = "Weight (grams) must be greater than 0")
    private int grams;
    private boolean seasonal;
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Recipe is required")
    private String recipe;
    @NotBlank(message = "Image URL is required")
    private String image;
    private List<IngredientDto> ingredients;
    private List<UUID> combinableProducts;
}
