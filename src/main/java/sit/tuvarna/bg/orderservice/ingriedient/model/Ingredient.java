package sit.tuvarna.bg.orderservice.ingriedient.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.product.module.Product;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ingredients")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;

    @Column(name = "vegan")
    private Boolean vegan;

    @Column(name = "allergen")
    private Boolean allergen;

    private Integer weightGrams;

    @Column(name = "stock_count")
    private Integer stockCount;

    @Enumerated(EnumType.STRING)
    private IngredientCategory category;

    @ManyToMany(mappedBy = "ingredients")
    private Set<Product> products;

}
