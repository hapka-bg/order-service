package sit.tuvarna.bg.orderservice.productReview.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.productReview.model.ProductReview;
import sit.tuvarna.bg.orderservice.productReview.model.ReviewRole;
import sit.tuvarna.bg.orderservice.web.dto.reviews.CountAvgPerRole;
import sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {
    Long countByReviewCreatedAtBetween(LocalDateTime start, LocalDateTime end);


    @Query(value = """
    SELECT new sit.tuvarna.bg.orderservice.web.dto.reviews.CountAvgPerRole(
    COUNT(p), AVG(p.rating)
    )
    FROM ProductReview p
    WHERE p.role = :givenRole
""")
    CountAvgPerRole getReviewsCountAndAvgForRole(@Param("givenRole") ReviewRole role);


    @Query("""
        SELECT new sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow(
            pr.userId,
            COUNT(pr),
            AVG(pr.rating),
            ""
        )
        FROM ProductReview pr
        WHERE pr.role = :role
        GROUP BY pr.userId
        ORDER BY AVG(pr.rating) DESC, COUNT(pr) DESC
    """)
    List<RankingRow> findTop3BestBartenders(Pageable pageable,@Param("role")  ReviewRole role);

    @Query("""
        SELECT new sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow(
            pr.userId,
            COUNT(pr),
            AVG(pr.rating),
            ""
        )
        FROM ProductReview pr
        WHERE pr.role = :role
        GROUP BY pr.userId
        ORDER BY AVG(pr.rating) ASC, COUNT(pr) DESC
    """)
    List<RankingRow> findTop3WorstBartenders(Pageable pageable,@Param("role")  ReviewRole role);

    List<ProductReview> findByReviewCreatedAtGreaterThanEqualAndRatingBetweenOrderByReviewCreatedAtDesc(LocalDateTime review_createdAt, Integer rating, Integer rating2);
}
