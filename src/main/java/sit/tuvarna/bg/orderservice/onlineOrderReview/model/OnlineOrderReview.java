package sit.tuvarna.bg.orderservice.onlineOrderReview.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "online_orders_reviews")
public class OnlineOrderReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "online_order_id")
    private OnlineOrder onlineOrder;

    @Column(name = "quality_review")
    private Integer qualityReview;

    @Column(name = "delivery_review")
    private Integer deliveryReview;

    @Column(name = "time_period")
    private String timePeriod;

    private String comment;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
