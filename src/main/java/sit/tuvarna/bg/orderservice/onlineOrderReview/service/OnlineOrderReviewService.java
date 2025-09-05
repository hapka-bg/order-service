package sit.tuvarna.bg.orderservice.onlineOrderReview.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.onlineOrderReview.repository.OnlineOrderReviewRepository;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.DeliveryVsSatisfactionDTO;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.OnlineOrderReviewsDisplay;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OnlineOrderReviewService {
    private final OnlineOrderReviewRepository onlineOrderReviewRepository;

    @Autowired
    public OnlineOrderReviewService(OnlineOrderReviewRepository onlineOrderReviewRepository) {
        this.onlineOrderReviewRepository = onlineOrderReviewRepository;
    }


    public List<OnlineOrderReviewsDisplay> getReviews(LocalDateTime from, LocalDateTime to){
       return onlineOrderReviewRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to)
                .stream()
                .map(onlineReview->OnlineOrderReviewsDisplay.builder()
                        .timePeriod(onlineReview.getTimePeriod())
                        .qualityReview(onlineReview.getQualityReview())
                        .deliveryReview(onlineReview.getDeliveryReview())
                        .comment(onlineReview.getComment())
                        .build()).toList();
    }


    public List<DeliveryVsSatisfactionDTO> getDataForSatisfaction(){
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return onlineOrderReviewRepository.findDeliveryVsSatisfactionFromDate(sevenDaysAgo);
    }

}
