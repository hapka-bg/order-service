package sit.tuvarna.bg.orderservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.orderservice.product.service.ProductService;
import sit.tuvarna.bg.orderservice.web.dto.products.CustomProduct;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {

    private final ProductService productService;

    @Autowired
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomProduct>> all(){
        System.out.println("Get all products");
        List<CustomProduct> allProducts = productService.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }
}
