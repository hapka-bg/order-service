package sit.tuvarna.bg.orderservice.reveiw.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.reveiw.model.Review;
import sit.tuvarna.bg.orderservice.web.dto.reviews.CountAvgPerRole;
import sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Long countByCreatedAtBetween(LocalDateTime createdAt, LocalDateTime createdAt2);

    @Query(value = """
            SELECT new sit.tuvarna.bg.orderservice.web.dto.reviews.CountAvgPerRole(
            COUNT(r), AVG(r.overallRating)
            )
            from Review r
            """)
    CountAvgPerRole getReviewsCountAndAvg();


    @Query(value = """
                SELECT new sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow(
                            b.userId,
                           COUNT(r),
                            AVG(r.overallRating),
                            "")
                FROM Review r
                JOIN Bill b ON b.session=r.session
                GROUP BY b.userId
               ORDER BY AVG(r.overallRating) DESC, COUNT(r) DESC
            """)
    List<RankingRow> getTop3BestWaiters(Pageable pageable);


    @Query("""
                SELECT new sit.tuvarna.bg.orderservice.web.dto.reviews.RankingRow(
                    b.userId,
                    COUNT(r) ,
                    AVG(r.overallRating),
                    '')
                FROM Review r
                JOIN Bill b ON b.session = r.session
                GROUP BY b.userId
                ORDER BY AVG(r.overallRating) ASC, COUNT(r) DESC
            """)
    List<RankingRow> getTop3WorstWaiters(Pageable pageable);

    List<Review> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime fromDate);
}
