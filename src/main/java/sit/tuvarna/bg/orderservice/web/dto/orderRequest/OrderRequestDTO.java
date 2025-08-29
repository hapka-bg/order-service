package sit.tuvarna.bg.orderservice.web.dto.orderRequest;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private List<ProductDTO> products;
    private ExtrasDTO extras;
    private String promoCode;
    private double total;
}
