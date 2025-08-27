package sit.tuvarna.bg.orderservice.web.dto.addProduct;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductResult {
    private String status;
    private String message;
    private UUID id;

}
