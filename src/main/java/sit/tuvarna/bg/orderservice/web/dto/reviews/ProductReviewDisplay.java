package sit.tuvarna.bg.orderservice.web.dto.reviews;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductReviewDisplay {
    private String productName;
    private Integer rating;
    private String comment;
}
