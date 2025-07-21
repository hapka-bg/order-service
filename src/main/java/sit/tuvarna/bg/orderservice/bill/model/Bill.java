package sit.tuvarna.bg.orderservice.bill.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import sit.tuvarna.bg.orderservice.onlineOrder.model.PaymentMethod;
import sit.tuvarna.bg.orderservice.tableSession.model.TableSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private TableSession session;

    private BigDecimal total;

    private BigDecimal discount;

    private BigDecimal tax;

    @Column(name = "final_total")
    private BigDecimal finalTotal;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "issued_at")
    @CreationTimestamp
    private LocalDateTime issuedAt;
}
