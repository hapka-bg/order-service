package sit.tuvarna.bg.orderservice.productCustomization.module;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.ingriedient.module.Ingredient;
import sit.tuvarna.bg.orderservice.product.module.Product;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@Table(name = "product_customization")
//@IdClass()
public class ProductCustomization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "can_add")
    private Boolean canAdd;

    @Column(name = "can_remove")
    private Boolean canRemove;

    @Column(name = "extra_cost")
    private BigDecimal extraCost;

    @Column(name ="max_quantity")
    private Integer maxQuantity;

}
