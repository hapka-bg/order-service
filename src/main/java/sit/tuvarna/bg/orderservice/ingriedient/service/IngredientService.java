package sit.tuvarna.bg.orderservice.ingriedient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.ingriedient.repository.IngredientRepository;
import sit.tuvarna.bg.orderservice.web.dto.heatmap.InventoryHeatMapRow;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository inventoryRepository;

    @Autowired
    public IngredientService(IngredientRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<InventoryHeatMapRow> getDataForHeatmap() {
        return inventoryRepository.getInventoryHeatmapCounts();
    }
}
