package sit.tuvarna.bg.orderservice.onlineOrder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import sit.tuvarna.bg.orderservice.onlineOrderItem.model.OnlineOrderItem;
import sit.tuvarna.bg.orderservice.onlineOrderReview.model.OnlineOrderReview;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "online_orders")
@Data
@NoArgsConstructor
public class OnlineOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "online_order_status", length = 50)
    private OnlineOrderStatus onlineOrderStatus;

    private BigDecimal total;
    private BigDecimal discount;
    private BigDecimal tax;

    @Column(name = "final_total")
    private BigDecimal finalTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method")
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnlineOrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "onlineOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnlineOrderReview> reviews = new ArrayList<>();
}
