package sit.tuvarna.bg.orderservice.bill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.bill.model.Bill;
import sit.tuvarna.bg.orderservice.web.dto.analytics.OrdersPerDay;
import sit.tuvarna.bg.orderservice.web.dto.analytics.WaiterOrders;
import sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.SumCount;
import sit.tuvarna.bg.orderservice.web.dto.salesChart.HourlyCountDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    @Query("""
            SELECT COALESCE(SUM(b.finalTotal),0 )
            from Bill b
            where b.issuedAt >= :startOfDay and b.issuedAt<= :endOfDay
            """)
    BigDecimal sumFromTotal(@Param("startOfDay") LocalDateTime startOfDay,
                            @Param("endOfDay") LocalDateTime endOfDay);

    Long countBillByIssuedAtBetween(LocalDateTime issuedAtBefore, LocalDateTime issuedAtAfter);

    @Query(value = """
            SELECT HOUR(issued_at) AS hour, COUNT(*) AS count
            FROM bills
            WHERE DATE(issued_at) = :targetDate
              AND HOUR(issued_at) BETWEEN 8 AND 19
            GROUP BY HOUR(issued_at)
            ORDER BY hour
            """, nativeQuery = true)
    List<HourlyCountDto> countByHour(@Param("targetDate") LocalDate targetDate);


    @Query("""
                SELECT new sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.SumCount(
                    COALESCE(SUM(b.finalTotal), CAST(0 AS Bigdecimal)),
                    CAST(COUNT(b) as Long)
                )
                FROM Bill b
                WHERE b.issuedAt >= :startDt AND b.issuedAt < :endDt
            """)
    SumCount sumAndCountBills(@Param("startDt") LocalDateTime startDt,
                              @Param("endDt") LocalDateTime endDt);


    @Query("""
                SELECT new sit.tuvarna.bg.orderservice.web.dto.analytics.OrdersPerDay(
                    CAST(b.issuedAt AS LocalDate),
                    COUNT(b)
                )
                FROM Bill b
                WHERE b.issuedAt >= :fromDate
                GROUP BY FUNCTION('date', b.issuedAt)
                ORDER BY FUNCTION('date', b.issuedAt)
            """)
    List<OrdersPerDay> findBillsPerDay(@Param("fromDate") LocalDateTime fromDate);


    @Query("""
                SELECT new sit.tuvarna.bg.orderservice.web.dto.analytics.WaiterOrders(
                    b.userId,
                    COUNT(b),
                    ""
                )
                FROM Bill b
                WHERE b.issuedAt >= :startDt
                  AND b.issuedAt < :endDt
                  AND b.userId IS NOT NULL
                GROUP BY b.userId
                ORDER BY COUNT(b) DESC
            """)
    List<WaiterOrders> countOrdersByWaiter(
            @Param("startDt") LocalDateTime startDt,
            @Param("endDt") LocalDateTime endDt);


}
