package sit.tuvarna.bg.orderservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.orderservice.ingriedient.service.IngredientService;
import sit.tuvarna.bg.orderservice.web.dto.addProduct.IngredientsData;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingredients")
public class IngredientsController {

    private final IngredientService ingredientService;

    @Autowired
    public IngredientsController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<IngredientsData>> getAllIngredients() {
        List<IngredientsData> allIngredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(allIngredients);
    }
}
