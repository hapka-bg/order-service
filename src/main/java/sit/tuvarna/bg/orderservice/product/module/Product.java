package sit.tuvarna.bg.orderservice.product.module;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.ingriedient.model.Ingredient;
import sit.tuvarna.bg.orderservice.productCustomization.module.ProductCustomization;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(name = "image_url")
    private String imageURL;

    private BigDecimal price;

    private Integer grams;

    @Column(name = "seasonal")
    private Boolean seasonal;

    private String description;

    @Column(name = "available")
    private Boolean available;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "display_order")
    private Integer displayOrder;

    private String recipe;

    @ManyToMany
    @JoinTable(name = "product_combinations",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "combine_with_product_id")
    )
    private Set<Product> combinations=new HashSet<>();

    @OneToMany(mappedBy = "product")
    private List<ProductCustomization> customizations=new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "product_to_ingredient",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> ingredients=new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
