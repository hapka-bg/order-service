package sit.tuvarna.bg.orderservice.onlineOrder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrder;

import java.util.UUID;

@Repository
public interface OnlineOrderRepository extends JpaRepository<OnlineOrder, UUID> {
}
