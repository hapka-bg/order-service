package sit.tuvarna.bg.orderservice.web.dto.addProduct;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductDto {

    private String name;
    private String description;
    private double price;
    private int grams;
    private boolean seasonal;
    private String category;
    private String recipe;
    private String image;
    private List<IngredientDto> ingredients;
    private List<UUID> combinableProducts;
}
