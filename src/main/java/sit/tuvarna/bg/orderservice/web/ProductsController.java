package sit.tuvarna.bg.orderservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.tuvarna.bg.orderservice.product.service.ProductService;
import sit.tuvarna.bg.orderservice.web.dto.addProduct.ProductDto;
import sit.tuvarna.bg.orderservice.web.dto.addProduct.ProductResult;
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
        List<CustomProduct> allProducts = productService.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }

    @PostMapping("/add")
    public ResponseEntity<ProductResult> addProduct(@RequestBody ProductDto productDto) {
        ProductResult productResult = productService.addNewProduct(productDto);
        return ResponseEntity.ok(productResult);
    }
}
