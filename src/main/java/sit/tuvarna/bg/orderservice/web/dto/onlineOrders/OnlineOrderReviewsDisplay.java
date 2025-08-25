package sit.tuvarna.bg.orderservice.web.dto.onlineOrders;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OnlineOrderReviewsDisplay {
    private Integer qualityReview;
    private Integer deliveryReview;
    private String timePeriod;
    private String comment;
}
