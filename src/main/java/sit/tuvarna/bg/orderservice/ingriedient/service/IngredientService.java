package sit.tuvarna.bg.orderservice.ingriedient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.exceptions.IngredientNotFoundException;
import sit.tuvarna.bg.orderservice.ingriedient.model.Ingredient;
import sit.tuvarna.bg.orderservice.ingriedient.repository.IngredientRepository;
import sit.tuvarna.bg.orderservice.web.dto.addProduct.IngredientsData;
import sit.tuvarna.bg.orderservice.web.dto.heatmap.InventoryHeatMapRow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public List<IngredientsData> getAllIngredients(){
        List<Ingredient> all = inventoryRepository.findAll();
        List<IngredientsData> result = new ArrayList<>();
        for (Ingredient ingredient : all) {
            IngredientsData build = IngredientsData.builder()
                    .id(ingredient.getId())
                    .name(ingredient.getName())
                    .build();
            result.add(build);
        }
        return result;
    }


    public List<Ingredient> findAllById(List<UUID> uuids){
        return inventoryRepository.findAllById(uuids);
    }

    public Ingredient getById(UUID id) {
        return inventoryRepository.findById(id).orElseThrow(() -> new IngredientNotFoundException("Ingredient not found"));
    }
}
