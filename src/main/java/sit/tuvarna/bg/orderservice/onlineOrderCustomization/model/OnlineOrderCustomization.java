package sit.tuvarna.bg.orderservice.onlineOrderCustomization.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.ingriedient.model.Ingredient;
import sit.tuvarna.bg.orderservice.onlineOrderItem.model.OnlineOrderItem;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "online_order_customizations")
public class OnlineOrderCustomization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OnlineOrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Integer quantity;

    @Column(name = "extra_cost")
    private BigDecimal extraCost;
}
