package sit.tuvarna.bg.orderservice.reveiw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.productReview.model.ReviewRole;
import sit.tuvarna.bg.orderservice.productReview.service.ProductReviewService;
import sit.tuvarna.bg.orderservice.reveiw.model.Review;
import sit.tuvarna.bg.orderservice.reveiw.repository.ReviewRepository;
import sit.tuvarna.bg.orderservice.web.dto.reviews.CountAvgPerRole;
import sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow;
import sit.tuvarna.bg.orderservice.web.dto.reviews.ReviewDisplay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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
        Map<String,CountAvgPerRole> result=new HashMap<>();
        result.put("waiters",reviewsForWaiter);
        result.put("chefs",reviewsForChef);
        result.put("bartenders",reviewsForBartender);
        return result;
    }

    public Map<String, List<RankingRow>> getBestAndWorst() {
        List<RankingRow> top3BestWaiters = reviewRepository.getTop3BestWaiters(PageRequest.of(0, 3));
        List<RankingRow> top3WorstWaiters = reviewRepository.getTop3WorstWaiters(PageRequest.of(0, 3));

        Map<String,List<RankingRow>> result=new HashMap<>();
        result.put("best",top3BestWaiters);
        result.put("worst",top3WorstWaiters);
        return result;
    }

    public List<ReviewDisplay> getTimeBasedReviews(String period){
        List<Review> result=new ArrayList<>();
        switch (period.toLowerCase()) {
            case "7days":
            case "week":
            {
                LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
                result = reviewRepository.findByCreatedAtAfterOrderByCreatedAtDesc(sevenDaysAgo);
            }
            break;

            case "30d":
            case "month":{
                LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
                result= reviewRepository.findByCreatedAtAfterOrderByCreatedAtDesc(monthAgo);
            }
            break;
            default:{
                //default for 7 days
                LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
                result = reviewRepository.findByCreatedAtAfterOrderByCreatedAtDesc(sevenDaysAgo);
            }
        }
        List<ReviewDisplay> resultToDisplay=new ArrayList<>();

        for (Review review : result) {
            ReviewDisplay build = ReviewDisplay.builder()
                    .comment(review.getComment())
                    .overallRating(review.getOverallRating())
                    .createdAt(review.getCreatedAt())
                    .build();
            resultToDisplay.add(build);
        }
        return resultToDisplay;
    }
}
