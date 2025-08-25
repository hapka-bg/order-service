package sit.tuvarna.bg.orderservice.web.dto.reviews;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter

public class RankingRow {
    private UUID userId;
    private Long reviewCount;
    private Double avgRating;
    @Setter
    private String name;

    public RankingRow(UUID userId, Long reviewCount, Double avgRating, String name) {
        this.userId = userId;
        this.reviewCount = reviewCount;
        this.avgRating = avgRating;
        this.name = name;
    }
}
