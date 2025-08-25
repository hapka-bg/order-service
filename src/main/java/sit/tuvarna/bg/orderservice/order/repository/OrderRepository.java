package sit.tuvarna.bg.orderservice.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.order.model.Order;
import sit.tuvarna.bg.orderservice.web.dto.analytics.CategoryOrders;
import sit.tuvarna.bg.orderservice.web.dto.orders.OrderSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
               SELECT new sit.tuvarna.bg.orderservice.web.dto.orders.OrderSummary(
                 MIN(o.id),
                 b.userId,
                 b.issuedAt,
                 b.finalTotal,
                 o.status,
                 ''
             )
             FROM Order o
             JOIN Bill b ON b.session.id = o.session.id
             WHERE b.issuedAt >= :sevenDaysAgo
             GROUP BY
                 b.userId,
                 b.issuedAt,
                 b.finalTotal,
                 o.status
            """)
    List<OrderSummary> findOrderSummaries(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);


    @Query("""
    SELECT new sit.tuvarna.bg.orderservice.web.dto.analytics.CategoryOrders(
        p.category,
        COUNT(o.id)
    )
    FROM Order o
    JOIN o.product p
    GROUP BY p.category
    ORDER BY COUNT(o.id) DESC
""")
    List<CategoryOrders> findPopularCategories();
}