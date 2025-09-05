package sit.tuvarna.bg.orderservice.reveiw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.productReview.model.ReviewRole;
import sit.tuvarna.bg.orderservice.productReview.service.ProductReviewService;
import sit.tuvarna.bg.orderservice.reveiw.repository.ReviewRepository;
import sit.tuvarna.bg.orderservice.web.dto.reviews.CountAvgPerRole;
import sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow;
import sit.tuvarna.bg.orderservice.web.dto.reviews.ReviewDisplay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductReviewService productReviewService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ProductReviewService productReviewService) {
        this.reviewRepository = reviewRepository;
        this.productReviewService = productReviewService;
    }

    public Long getNewReviews(){
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return reviewRepository.countByCreatedAtBetween(startOfDay, endOfDay);
    }

    public Map<String, CountAvgPerRole> getReviewsCountAndAvg(){
        CountAvgPerRole reviewsForWaiter = reviewRepository.getReviewsCountAndAvg();
        CountAvgPerRole reviewsForChef = productReviewService.getCountAndAvgForRole(ReviewRole.CHEF);
        CountAvgPerRole reviewsForBartender = productReviewService.getCountAndAvgForRole(ReviewRole.BARTENDER);
        return Map.of(
                "waiters",reviewsForWaiter,
                "chefs",reviewsForChef,
                "bartenders",reviewsForBartender
        );
    }

    public Map<String, List<RankingRow>> getBestAndWorst() {
        List<RankingRow> top3BestWaiters = reviewRepository.getTop3BestWaiters(PageRequest.of(0, 3));
        List<RankingRow> top3WorstWaiters = reviewRepository.getTop3WorstWaiters(PageRequest.of(0, 3));

        return Map.of("best",top3BestWaiters,
                        "worst",top3WorstWaiters);

    }

    public List<ReviewDisplay> getTimeBasedReviews(String period){

        LocalDateTime fromDate =   switch(period.toLowerCase().trim()){
            case "7days", "week" -> LocalDateTime.now().minusDays(7);
            case "30d", "month" -> LocalDateTime.now().minusDays(30);
            default -> LocalDateTime.now().minusDays(7);
        };

        return reviewRepository.findByCreatedAtAfterOrderByCreatedAtDesc(fromDate).stream()
                .map(review -> ReviewDisplay.builder()
                        .comment(review.getComment())
                        .overallRating(review.getOverallRating())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();
    }
}
