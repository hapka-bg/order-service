package sit.tuvarna.bg.orderservice.web.dto.heatmap;

import lombok.Data;
import sit.tuvarna.bg.orderservice.ingriedient.model.IngredientCategory;

import java.util.List;

@Data
public class InventoryHeatMapRow {
    private String category;
    private List<Long> counts;

    public InventoryHeatMapRow(IngredientCategory category, Long range0to10,
                               Long range11to50, Long range51to100,
                               Long range100plus) {
        this.category = category.name();
        this.counts = List.of(range0to10, range11to50, range51to100, range100plus);
    }
}
