package sit.tuvarna.bg.orderservice.onlineOrder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OnlineOrderRepository extends JpaRepository<OnlineOrder, UUID> {

    @Query("""
    SELECT COALESCE(SUM(o.finalTotal),0 ) from OnlineOrder o where o.createdAt >= :startOfDay and o.createdAt<= :endOfDay
    """)
    BigDecimal sumFromTotal(@Param("startOfDay") LocalDateTime startOfDay,
                            @Param("endOfDay") LocalDateTime endOfDay);
}
