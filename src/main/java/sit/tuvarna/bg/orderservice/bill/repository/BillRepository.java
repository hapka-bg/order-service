package sit.tuvarna.bg.orderservice.bill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.bill.model.Bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    @Query("""
    SELECT COALESCE(SUM(b.finalTotal),0 ) from Bill b where b.issuedAt >= :startOfDay and b.issuedAt<= :endOfDay
    """)
    BigDecimal sumFromTotal(@Param("startOfDay") LocalDateTime startOfDay,
                            @Param("endOfDay") LocalDateTime endOfDay);

    Long countBillByIssuedAtBetween(LocalDateTime issuedAtBefore, LocalDateTime issuedAtAfter);
}
