package sit.tuvarna.bg.orderservice.productReview.model;


import jakarta.persistence.*;
import lombok.Data;
import sit.tuvarna.bg.orderservice.product.module.Product;
import sit.tuvarna.bg.orderservice.reveiw.model.Review;

import java.util.UUID;

@Entity
@Data
@Table(name = "product_reviews")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private UUID userId;

    private Integer rating;

    @Enumerated(EnumType.STRING)
    private ReviewRole role;
}
