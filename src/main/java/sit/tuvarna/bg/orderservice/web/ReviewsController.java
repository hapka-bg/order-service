package sit.tuvarna.bg.orderservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.tuvarna.bg.orderservice.productReview.service.ProductReviewService;
import sit.tuvarna.bg.orderservice.reveiw.service.ReviewService;
import sit.tuvarna.bg.orderservice.utils.ReviewFacadeService;
import sit.tuvarna.bg.orderservice.web.dto.reviews.CountAvgPerRole;
import sit.tuvarna.bg.orderservice.web.dto.reviews.ProductReviewDisplay;
import sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow;
import sit.tuvarna.bg.orderservice.web.dto.reviews.ReviewDisplay;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewsController {

    private final ReviewService reviewService;
    private final ReviewFacadeService reviewFacadeService;
    private final ProductReviewService productReviewService;

    @Autowired
    public ReviewsController(ReviewService reviewService, ReviewFacadeService reviewFacadeService, ProductReviewService productReviewService) {
        this.reviewService = reviewService;
        this.reviewFacadeService = reviewFacadeService;
        this.productReviewService = productReviewService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, CountAvgPerRole>> getSummary() {
        Map<String, CountAvgPerRole> reviewsCountAndAvg = reviewService.getReviewsCountAndAvg();
        return ResponseEntity.ok(reviewsCountAndAvg);
    }

    @GetMapping("/{roleKey}")
    public ResponseEntity<Map<String, List<RankingRow>>> getRoleDetails(@PathVariable String roleKey) {
        Map<String, List<RankingRow>> roleBasedReview = reviewFacadeService.getRoleBasedReview(roleKey);
        return ResponseEntity.ok(roleBasedReview);
    }

    //time-based reviews
    @GetMapping
    public ResponseEntity< List<ReviewDisplay>> getAll(@RequestParam String period) {
        List<ReviewDisplay> timeBasedReviews = reviewService.getTimeBasedReviews(period);
        return ResponseEntity.ok(timeBasedReviews);
    }

    @GetMapping("/product")
    public ResponseEntity< List<ProductReviewDisplay>> getProductDetails(@RequestParam String sentiment) {
        List<ProductReviewDisplay> positiveOrNegativeReviews = productReviewService.getPositiveOrNegativeReviews(sentiment);
        return ResponseEntity.ok(positiveOrNegativeReviews);
    }
}
