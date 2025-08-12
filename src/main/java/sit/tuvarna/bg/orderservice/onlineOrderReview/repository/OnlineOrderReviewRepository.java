package sit.tuvarna.bg.orderservice.onlineOrderReview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.onlineOrderReview.model.OnlineOrderReview;

import java.util.UUID;

@Repository
public interface OnlineOrderReviewRepository extends JpaRepository<OnlineOrderReview, UUID> {
}
