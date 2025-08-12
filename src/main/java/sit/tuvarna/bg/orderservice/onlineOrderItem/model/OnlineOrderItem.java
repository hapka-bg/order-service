package sit.tuvarna.bg.orderservice.onlineOrderItem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrder;
import sit.tuvarna.bg.orderservice.onlineOrderCustomization.model.OnlineOrderCustomization;
import sit.tuvarna.bg.orderservice.product.module.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "online_order_items")
public class OnlineOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "online_order_id")
    private OnlineOrder order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    private String notes;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnlineOrderCustomization> customizations = new ArrayList<>();
}
