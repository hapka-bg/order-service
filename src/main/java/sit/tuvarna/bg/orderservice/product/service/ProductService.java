package sit.tuvarna.bg.orderservice.product.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import sit.tuvarna.bg.orderservice.web.dto.users.*;

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

    public List<ProductsDto> getAllForUser( ){
        List<ProductsDto> result=new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            ProductsDto productDto = new ProductsDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setCategory(product.getCategory().name());
            productDto.setWeight(product.getGrams());
            productDto.setPrice(product.getPrice());
            productDto.setImage(product.getImageURL());
            result.add(productDto);
        }
        return result;
    }

    public ProductToDisplay getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductToDisplay dto = new ProductToDisplay();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setWeight(product.getGrams());
        dto.setImage(product.getImageURL());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory().name());
        dto.setRecipe(product.getRecipe());
        dto.setCustomizations(product.getCustomizations().stream().map(c -> {
            CustomizationDto cdto = new CustomizationDto();
            cdto.setId(c.getId());
            CustomizableIngredients ing = new CustomizableIngredients();
            ing.setId(c.getIngredient().getId());
            ing.setName(c.getIngredient().getName());
            cdto.setIngredient(ing);
            cdto.setAddable(Boolean.TRUE.equals(c.getAddable()));
            cdto.setRemovable(Boolean.TRUE.equals(c.getRemovable()));
            cdto.setExtraCost(c.getExtraCost());
            cdto.setMaxQuantity(c.getMaxQuantity());
            return cdto;
        }).collect(Collectors.toList()));

        dto.setRecommended(product.getCombinations().stream().map(r -> {
            RecommendedProductDto recDto = new RecommendedProductDto();
            recDto.setId(r.getId());
            recDto.setName(r.getName());
            recDto.setPrice(r.getPrice());
            recDto.setImage(r.getImageURL());
            return recDto;
        }).collect(Collectors.toList()));
        return dto;
    }

    public Product getById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void updateProduct(UUID id, ProductDto dto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(BigDecimal.valueOf(dto.getPrice()));
        product.setGrams(dto.getGrams());
        product.setSeasonal(dto.isSeasonal());
        product.setCategory(Category.valueOf(dto.getCategory())); // must match enum
        product.setRecipe(dto.getRecipe());
        product.setImageURL(dto.getImage());

        Set<Ingredient> ingredients = ingredientService.findAllById(
                dto.getIngredients().stream().map(IngredientDto::getId).toList()
        ).stream().collect(Collectors.toSet());
        product.getIngredients().clear();
        product.getIngredients().addAll(ingredients);

        product.getCustomizations().clear();

        Map<Boolean, List<IngredientDto>> partitionedIngredients =
                dto.getIngredients().stream()
                        .collect(Collectors.partitioningBy(IngredientDto::isPermanent));

        List<IngredientDto> nonPermanentIngredients = partitionedIngredients.get(false);
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
            product.getCustomizations().add(productCustomization);
        }

        // 5. Update combinations
        Set<Product> combinations = productRepository.findAllById(dto.getCombinableProducts())
                .stream().collect(Collectors.toSet());
        product.getCombinations().clear();
        product.getCombinations().addAll(combinations);

        // 6. Save updated product
        Product saved = productRepository.save(product);

        // 7. Handle image upload asynchronously
        if (dto.getImage() != null && !dto.getImage().isBlank()) {
            uploadBase64ImageAsync(dto.getImage())
                    .thenAccept(imageUrl -> {
                        product.setImageURL(imageUrl);
                        productRepository.save(product);
                    })
                    .exceptionally(ex -> {
                        System.err.println("Image upload failed: " + ex.getMessage());
                        return null;
                    });
        }

    }
}
