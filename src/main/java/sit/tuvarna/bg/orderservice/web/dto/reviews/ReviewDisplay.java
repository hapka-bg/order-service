package sit.tuvarna.bg.orderservice.web.dto.reviews;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ReviewDisplay {
    private LocalDateTime createdAt;
    private Integer overallRating;
    private String comment;
}
