package sit.tuvarna.bg.orderservice.onlineOrderItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.onlineOrderItem.model.OnlineOrderItem;
import sit.tuvarna.bg.orderservice.web.dto.top5.ProductSparkProjection;

import java.util.List;
import java.util.UUID;

@Repository
public interface OnlineOrderItemRepository extends JpaRepository<OnlineOrderItem, UUID> {

    //top 5 products
    @Query(value = """
         SELECT LOWER(BIN_TO_UUID(product_id)) AS productId,
          JSON_ARRAYAGG(units) AS unitsArray
      FROM (
          SELECT
              s.product_id,
              COALESCE(daily.units, 0) AS units
          FROM (
              SELECT DISTINCT product_id
              FROM online_order_items i
              JOIN online_orders o ON o.id = i.online_order_id
              WHERE o.created_at >= CURDATE() - INTERVAL 6 DAY
                AND o.created_at <  CURDATE() + INTERVAL 1 DAY
          ) s
          JOIN (
              SELECT DATE(CURDATE() - INTERVAL n DAY) AS day
              FROM (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL\s
                    SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6) nums
          ) d
          LEFT JOIN (
              SELECT\s
                  i.product_id,
                  DATE(o.created_at) AS order_day,
                  SUM(i.quantity) AS units
              FROM online_order_items i
              JOIN online_orders o ON o.id = i.online_order_id
              WHERE o.created_at >= CURDATE() - INTERVAL 6 DAY
                AND o.created_at <  CURDATE() + INTERVAL 1 DAY
              GROUP BY i.product_id, DATE(o.created_at)
          ) daily
            ON daily.product_id = s.product_id AND daily.order_day = d.day
            ORDER BY d.day
      ) AS joined
      GROUP BY product_id
      ORDER BY SUM(units) DESC
      LIMIT 5
      """, nativeQuery = true)
    List<ProductSparkProjection> fetchProductSparks();
}
