package sit.tuvarna.bg.orderservice.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.ingriedient.model.Ingredient;
import sit.tuvarna.bg.orderservice.ingriedient.service.IngredientService;
import sit.tuvarna.bg.orderservice.product.module.Category;
import sit.tuvarna.bg.orderservice.product.module.Product;
import sit.tuvarna.bg.orderservice.product.repository.ProductRepository;
import sit.tuvarna.bg.orderservice.productCustomization.module.ProductCustomization;
import sit.tuvarna.bg.orderservice.productCustomization.service.ProductCustomizationService;
import sit.tuvarna.bg.orderservice.utils.CloudinaryService;
import sit.tuvarna.bg.orderservice.web.dto.addProduct.IngredientDto;
import sit.tuvarna.bg.orderservice.web.dto.addProduct.ProductDto;
import sit.tuvarna.bg.orderservice.web.dto.addProduct.ProductResult;
import sit.tuvarna.bg.orderservice.web.dto.products.CustomProduct;
import sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.ProductExtremesProjection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final IngredientService ingredientService;
    private final ProductCustomizationService productCustomizationService;

    @Autowired
    public ProductService(ProductRepository productRepository, CloudinaryService cloudinaryService, IngredientService ingredientService, ProductCustomizationService productCustomizationService) {
        this.productRepository = productRepository;
        this.cloudinaryService = cloudinaryService;
        this.ingredientService = ingredientService;
        this.productCustomizationService = productCustomizationService;
    }

    public List<CustomProduct> getAllProducts() {
        List<Product> all = productRepository.findAll();
        List<CustomProduct> result=new ArrayList<>(all.size());
        for (Product product : all) {
            CustomProduct build = CustomProduct.builder()
                    .id(product.getId())
                    .price(product.getPrice())
                    .name(product.getName())
                    .imageURL(product.getImageURL())
                    .description(product.getDescription())
                    .build();
            result.add(build);
        }
        return result;
    }

    public ProductExtremesProjection getMostAndLeastOrdered(LocalDateTime startDt, LocalDateTime endDt) {
        return productRepository.findMostAndLeastOrderedProductNames(startDt, endDt);
    }

    @Async
    public CompletableFuture<String> uploadBase64ImageAsync(String base64Image) {
        if (base64Image != null && !base64Image.isBlank()) {
            String imageUrl = cloudinaryService.uploadBase64Image(base64Image);
            return CompletableFuture.completedFuture(imageUrl);
        }
        return CompletableFuture.completedFuture(base64Image);
    }
    public ProductResult addNewProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(BigDecimal.valueOf(productDto.getPrice()));
        product.setGrams(productDto.getGrams());
        product.setSeasonal(productDto.isSeasonal());
        product.setCategory(Category.valueOf(productDto.getCategory()));
        product.setRecipe(productDto.getRecipe());
        product.setAvailable(true);
        product.setDisplayOrder(0);

        Set<Ingredient> ingredients  = ingredientService.findAllById(
                productDto.getIngredients().stream().map(IngredientDto::getId).toList()
        ).stream().collect(Collectors.toSet());
        product.setIngredients(ingredients);


        Map<Boolean, List<IngredientDto>> partitionedIngredients =
                productDto.getIngredients().stream()
                        .collect(Collectors.partitioningBy(IngredientDto::isPermanent));

        List<IngredientDto> nonPermanentIngredients = partitionedIngredients.get(false);
        List<ProductCustomization> customizations = new ArrayList<>();
        Map<UUID, Ingredient> ingredientMap = ingredients.stream()
                .collect(Collectors.toMap(Ingredient::getId, i -> i));
        for (IngredientDto nonPermanentIngredient : nonPermanentIngredients) {
            ProductCustomization productCustomization = new ProductCustomization();
            productCustomization.setProduct(product);
            productCustomization.setIngredient(ingredientMap.get(nonPermanentIngredient.getId()));
            productCustomization.setAddable(nonPermanentIngredient.isCanBeAdded());
            productCustomization.setRemovable(nonPermanentIngredient.isCanBeRemoved());
            productCustomization.setExtraCost(nonPermanentIngredient.getExtraCost());
            productCustomization.setMaxQuantity(nonPermanentIngredient.getMaxQty());
            customizations.add(productCustomization);
        }
        product.setCustomizations(customizations);


        //combinations
        List<UUID> combinableProducts = productDto.getCombinableProducts();
        Set<Product> combinations = productRepository.findAllById(combinableProducts)
                .stream().collect(Collectors.toSet());
        product.setCombinations(combinations);

        Product save = productRepository.save(product);
        productCustomizationService.saveAllProductCustomization(customizations);

        uploadBase64ImageAsync(productDto.getImage())
                .thenAccept(imageUrl -> {
                    product.setImageURL(imageUrl);
                    productRepository.save(product); // update product with uploaded image URL
                })
                .exceptionally(ex -> {
                    // handle failure (optional logging)
                    System.err.println("Image upload failed: " + ex.getMessage());
                    return null;
                });


        ProductResult pr=new ProductResult();
        pr.setId(save.getId());
        pr.setStatus("success");
        pr.setMessage("Product added successfully");
        return pr;
    }

}
