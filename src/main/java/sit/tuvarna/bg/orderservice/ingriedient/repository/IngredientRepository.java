package sit.tuvarna.bg.orderservice.ingriedient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sit.tuvarna.bg.orderservice.ingriedient.model.Ingredient;
import sit.tuvarna.bg.orderservice.web.dto.heatmap.InventoryHeatMapRow;

import java.util.List;
import java.util.UUID;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {


    @Query("""
    SELECT new sit.tuvarna.bg.orderservice.web.dto.heatmap.InventoryHeatMapRow(
        category,
        COUNT(CASE WHEN stockCount BETWEEN 0 AND 10 THEN 1 END),
        COUNT(CASE WHEN stockCount BETWEEN 11 AND 50 THEN 1 END),
        COUNT(CASE WHEN stockCount BETWEEN 51 AND 100 THEN 1 END),
        COUNT(CASE WHEN stockCount > 100 THEN 1 END)
    )
    FROM Ingredient
    GROUP BY category
    ORDER BY category
""")
    List<InventoryHeatMapRow> getInventoryHeatmapCounts();

}
