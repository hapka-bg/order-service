package sit.tuvarna.bg.orderservice.productReview.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.productReview.model.ProductReview;
import sit.tuvarna.bg.orderservice.productReview.model.ReviewRole;
import sit.tuvarna.bg.orderservice.productReview.repository.ProductReviewRepository;
import sit.tuvarna.bg.orderservice.web.dto.reviews.CountAvgPerRole;
import sit.tuvarna.bg.orderservice.web.dto.reviews.ProductReviewDisplay;
import sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductReviewService {


    private final ProductReviewRepository reviewRepository;

    @Autowired
    public ProductReviewService(ProductReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Long getNewReviews(){
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return reviewRepository.countByReviewCreatedAtBetween(startOfDay, endOfDay);
    }

    public CountAvgPerRole getCountAndAvgForRole(ReviewRole role){
        return reviewRepository.getReviewsCountAndAvgForRole(role);
    }

    public Map<String, List<RankingRow>> getBestAndWorst(ReviewRole role) {
        List<RankingRow> top3BestBartenders = reviewRepository.findTop3BestBartenders(PageRequest.of(0, 3),role);
        List<RankingRow> top3WorstBartenders = reviewRepository.findTop3WorstBartenders(PageRequest.of(0, 3),role);

        Map<String,List<RankingRow>> result=new HashMap<>();
        result.put("best",top3BestBartenders);
        result.put("worst",top3WorstBartenders);
        return result;
    }

    public List<ProductReviewDisplay> getPositiveOrNegativeReviews(String sentiment){
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<ProductReview> productReviews =new ArrayList<>();
        productReviews = switch (sentiment) {
            case "good" ->
                    reviewRepository.findByReviewCreatedAtGreaterThanEqualAndRatingBetweenOrderByReviewCreatedAtDesc(oneWeekAgo, 4, 5);
            case "bad" ->
                    reviewRepository.findByReviewCreatedAtGreaterThanEqualAndRatingBetweenOrderByReviewCreatedAtDesc(oneWeekAgo, 1, 3);
            default -> productReviews;
        };
        List<ProductReviewDisplay> result=new ArrayList<>();
        for (ProductReview productReview : productReviews) {
            ProductReviewDisplay build = ProductReviewDisplay.builder()
                    .comment(productReview.getReview().getComment())
                    .rating(productReview.getRating())
                    .productName(productReview.getProduct().getName())
                    .build();
            result.add(build);
        }
        return result;
    }
}
