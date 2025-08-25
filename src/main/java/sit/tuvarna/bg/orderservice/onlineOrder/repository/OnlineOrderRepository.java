package sit.tuvarna.bg.orderservice.onlineOrder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrder;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrderStatus;
import sit.tuvarna.bg.orderservice.web.dto.analytics.OrdersPerDay;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.OrdersPerDayDTO;
import sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.SumCount;
import sit.tuvarna.bg.orderservice.web.dto.salesChart.HourlyCountDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OnlineOrderRepository extends JpaRepository<OnlineOrder, UUID> {

    @Query("""
            SELECT COALESCE(SUM(o.finalTotal),0 )
            from OnlineOrder
            o where o.createdAt >= :startOfDay and o.createdAt<= :endOfDay
            """)
    BigDecimal sumFromTotal(@Param("startOfDay") LocalDateTime startOfDay,
                            @Param("endOfDay") LocalDateTime endOfDay);


    Long countOnlineOrderByCreatedAtBetween(LocalDateTime issuedAtBefore, LocalDateTime issuedAtAfter);

    @Query(value = """
            SELECT HOUR(created_at) AS hour, COUNT(*) AS count
            FROM online_orders
            WHERE DATE(created_at) = :targetDate
              AND HOUR(created_at) BETWEEN 8 AND 19
            GROUP BY HOUR(created_at)
            ORDER BY hour
            """, nativeQuery = true)
    List<HourlyCountDto> countByHour(@Param("targetDate") LocalDate targetDate);


    @Query("""
                SELECT new sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.SumCount(
                    COALESCE(SUM(o.finalTotal), CAST(0 AS Bigdecimal)),
                    CAST(COUNT(o) as Long)
                )
                FROM OnlineOrder o
                WHERE o.createdAt >= :startDt AND o.createdAt < :endDt
            """)
    SumCount sumAndCountOnlineOrders(@Param("startDt") LocalDateTime startDt,
                                     @Param("endDt") LocalDateTime endDt);


    @Query(value = """
            SELECT
                CASE
                    WHEN :grouping = 'daily' THEN DATE(ts)
                    WHEN :grouping = 'weekly' THEN YEARWEEK(ts, 1)
                    WHEN :grouping = 'monthly' THEN DATE_FORMAT(ts, '%Y-%m')
                END AS period,
                SUM(amount) AS revenue
            FROM (
                SELECT created_at AS ts, final_total AS amount
                FROM online_orders
                WHERE created_at >= :startDt AND created_at < :endDt
                UNION ALL
                SELECT issued_at AS ts, final_total AS amount
                FROM bills
                WHERE issued_at >= :startDt AND issued_at < :endDt
            ) t
            GROUP BY period
            ORDER BY period
            """, nativeQuery = true)
    List<Object[]> findRevenueByPeriod(@Param("startDt") LocalDateTime startDt,
                                       @Param("endDt") LocalDateTime endDt,
                                       @Param("grouping") String grouping);


    @Query("""
                SELECT new sit.tuvarna.bg.orderservice.web.dto.analytics.OrdersPerDay(
                     CAST(FUNCTION('date', o.createdAt) AS java.time.LocalDate),
                    COUNT(o)
                )
                FROM OnlineOrder o
                WHERE o.createdAt >= :startDate
            GROUP BY CAST(FUNCTION('date', o.createdAt) AS java.time.LocalDate)
                ORDER BY CAST(FUNCTION('date', o.createdAt) AS java.time.LocalDate)
            """)
    List<OrdersPerDay> findOnlineOrdersPerDay(@Param("startDate") LocalDateTime startDate);



    List<OnlineOrder> findAllByOnlineOrderStatusInOrderByCreatedAtDesc(List<OnlineOrderStatus> onlineOrderStatus);

    List<OnlineOrder> findAllByCreatedAtBetweenOrderByDeliveredAtDesc(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new sit.tuvarna.bg.orderservice.web.dto.onlineOrders.OrdersPerDayDTO(
        CAST(FUNCTION('date', o.createdAt) AS java.time.LocalDate),
        COUNT(o)
    )
    FROM OnlineOrder o
    WHERE o.createdAt >= :startDate
    GROUP BY CAST(FUNCTION('date', o.createdAt) AS java.time.LocalDate)
    ORDER BY CAST(FUNCTION('date', o.createdAt) AS java.time.LocalDate) ASC
""")
    List<OrdersPerDayDTO> countOrdersPerDay(@Param("startDate") LocalDateTime startDate);



}
