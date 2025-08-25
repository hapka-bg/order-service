package sit.tuvarna.bg.orderservice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.product.module.Product;
import sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.ProductExtremesProjection;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value = """
                WITH product_totals AS (
                   SELECT p.id, p.name, SUM(qty) AS total_units
                   FROM (
                       SELECT i.product_id, SUM(i.quantity) AS qty
                       FROM online_order_items i
                       JOIN online_orders o ON o.id = i.online_order_id
                       WHERE o.created_at >= :startDt AND o.created_at < :endDt
                       GROUP BY i.product_id
                       UNION ALL
                       SELECT o.product_id, SUM(o.quantity) AS qty
                       FROM orders o
                       JOIN bills b ON b.session_id = o.session_id
                       WHERE b.issued_at >= :startDt AND b.issued_at < :endDt
                       GROUP BY o.product_id
                   ) x
                   JOIN products p ON p.id = x.product_id
                   GROUP BY p.id, p.name
                               )
             SELECT
               (SELECT name FROM product_totals ORDER BY total_units DESC LIMIT 1) AS mostOrdered,
               (SELECT name FROM product_totals ORDER BY total_units LIMIT 1) AS leastOrdered
            """,nativeQuery = true)
    ProductExtremesProjection findMostAndLeastOrderedProductNames(@Param("startDt") LocalDateTime startDt,
                                                                  @Param("endDt") LocalDateTime endDt);
}
