package sit.tuvarna.bg.orderservice.web.dto.orderRequest;

import lombok.Data;

import java.util.List;

@Data
public class ProductCustomizationsDTO {
    private List<ProductCustomizationDTO> added;
    private List<ProductCustomizationDTO> removed;
}
