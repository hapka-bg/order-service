package sit.tuvarna.bg.orderservice.onlineOrderItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.onlineOrderItem.model.OnlineOrderItem;

import java.util.UUID;

@Repository
public interface OnlineOrderItemRepository extends JpaRepository<OnlineOrderItem, UUID> {
}
