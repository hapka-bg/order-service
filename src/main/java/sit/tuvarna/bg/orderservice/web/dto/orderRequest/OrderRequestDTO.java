package sit.tuvarna.bg.orderservice.web.dto.orderRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    @NotEmpty(message = "Order must contain at least one product.")
    private List<@Valid  ProductDTO> products;
    @NotNull(message = "Extras information is required.")
    private ExtrasDTO extras;
    @NotBlank(message = "Promo code cant be blank!")
    private String promoCode;
    @Positive(message = "Total price must be greater than 0.")
    private double total;
}
