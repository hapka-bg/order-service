package sit.tuvarna.bg.orderservice.ingriedient.module;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sit.tuvarna.bg.orderservice.product.module.Product;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(name = "is_vegan")
    private Boolean isVegan;

    @Column(name = "is_alergen")
    private Boolean isAllergen;

    private Integer weightGrams;

    @ManyToMany(mappedBy = "ingredients")
    private Set<Product> products;

}
