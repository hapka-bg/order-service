package sit.tuvarna.bg.orderservice.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.auth.service.AuthService;
import sit.tuvarna.bg.orderservice.productReview.model.ReviewRole;
import sit.tuvarna.bg.orderservice.productReview.service.ProductReviewService;
import sit.tuvarna.bg.orderservice.reveiw.service.ReviewService;
import sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReviewFacadeService {
    private final ReviewService reviewService;
    private final ProductReviewService productReviewService;
    private final AuthService authService;

    @Autowired
    public ReviewFacadeService(ReviewService reviewService, ProductReviewService productReviewService, AuthService authService) {
        this.reviewService = reviewService;
        this.productReviewService = productReviewService;
        this.authService = authService;
    }


    public Map<String, List<RankingRow>> getRoleBasedReview(String role) {
        Map<String, List<RankingRow>> result = switch (role) {
            case "waiters" -> reviewService.getBestAndWorst();
            case "bartenders" -> productReviewService.getBestAndWorst(ReviewRole.BARTENDER);
            case "chefs" -> productReviewService.getBestAndWorst(ReviewRole.CHEF);
            default -> null;
        };

        //we need to send that result to the auth service to fetch the names
        assert result != null;
        for (List<RankingRow> value : result.values()) {
            List<UUID> uuids = value.stream().map(RankingRow::getUserId).toList();
            List<String> names = authService.getNames(uuids);
            int index=0;
            for (RankingRow rankingRow : value) {
                rankingRow.setName(names.get(index++));
                if(index==names.size()) break;
            }
        }
        return result;
    }
}
