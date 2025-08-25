package sit.tuvarna.bg.orderservice.onlineOrderReview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.onlineOrderReview.model.OnlineOrderReview;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.DeliveryVsSatisfactionDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OnlineOrderReviewRepository extends JpaRepository<OnlineOrderReview, UUID> {


    List<OnlineOrderReview> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime createdAt, LocalDateTime createdAt2);


    @Query(value = """
    SELECT TIMESTAMPDIFF(MINUTE, o.created_at, o.delivered_at) AS x,
           r.delivery_review AS y
    FROM online_orders_reviews r
    JOIN online_orders o ON r.online_order_id = o.id
    WHERE r.created_at >= :fromDate
      AND o.delivered_at IS NOT NULL
      AND r.delivery_review IS NOT NULL
    ORDER BY r.created_at DESC
""", nativeQuery = true)
    List<DeliveryVsSatisfactionDTO> findDeliveryVsSatisfactionFromDate(@Param("fromDate") LocalDateTime fromDate);

}
