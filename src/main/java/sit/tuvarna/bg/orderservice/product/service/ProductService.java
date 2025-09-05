package sit.tuvarna.bg.orderservice.product.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.tuvarna.bg.orderservice.exceptions.MissingProductException;
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
      return  productRepository.findAll()
                .stream()
                .map(product ->CustomProduct.builder()
                        .id(product.getId())
                        .price(product.getPrice())
                        .name(product.getName())
                        .imageURL(product.getImageURL())
                        .description(product.getDescription())
                        .build()).toList();
    }

    public List<ProductsDto> getAllForUser( ){
        return productRepository.findAll()
                .stream()
                .map(this::toProductsDto).toList();
    }

    public ProductExtremesProjection getMostAndLeastOrdered(LocalDateTime startDt, LocalDateTime endDt) {
        return productRepository.findMostAndLeastOrderedProductNames(startDt, endDt);
    }

    @Async
    public CompletableFuture<String> uploadBase64ImageAsync(String base64Image) {
        if (base64Image != null && !base64Image.isBlank()) {
            return CompletableFuture.supplyAsync(() -> cloudinaryService.uploadBase64Image(base64Image));
        }
        return CompletableFuture.completedFuture(base64Image);
    }

    public ProductResult addNewProduct(ProductDto productDto) {
        Product product = new Product();
        populateProductFromDto(product,productDto);

        Product save = productRepository.save(product);
        productCustomizationService.saveAllProductCustomization(product.getCustomizations());

        if (productDto.getImage() != null && !productDto.getImage().isBlank()) {
            uploadBase64ImageAsync(productDto.getImage())
                    .thenAccept(url -> {
                        product.setImageURL(url);
                        productRepository.save(product);
                    })
                    .exceptionally(ex -> {
                        System.err.println("Image upload failed: " + ex.getMessage());
                        return null;
                    });
        }

        ProductResult pr=new ProductResult();
        pr.setId(save.getId());
        pr.setStatus("success");
        pr.setMessage("Product added successfully");
        return pr;
    }
    @Transactional
    public void updateProduct(UUID id, ProductDto dto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new MissingProductException("Product not found"));
        populateProductFromDto(product,dto);

        // 6. Save updated product
        productRepository.save(product);

        // 7. Handle image upload asynchronously
        if (dto.getImage() != null && !dto.getImage().isBlank()) {
            uploadBase64ImageAsync(dto.getImage())
                    .thenAccept(url -> {
                        product.setImageURL(url);
                        productRepository.save(product);
                    })
                    .exceptionally(ex -> {
                        System.err.println("Image upload failed: " + ex.getMessage());
                        return null;
                    });
        }
    }


    public ProductToDisplay getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new MissingProductException("Product not found"));
        return toProductToDisplay(product);
    }

    public Product getById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new MissingProductException("Product not found"));
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }



    private void populateProductFromDto(Product product, ProductDto dto){
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(BigDecimal.valueOf(dto.getPrice()));
        product.setGrams(dto.getGrams());
        product.setSeasonal(dto.isSeasonal());
        product.setCategory(Category.valueOf(dto.getCategory()));
        product.setRecipe(dto.getRecipe());

        // Ingredients
        Set<Ingredient> ingredients = new HashSet<>(ingredientService.findAllById(
                dto.getIngredients().stream().map(IngredientDto::getId).toList()
        ));
        product.setIngredients(ingredients);

        // Customizations
        List<IngredientDto> nonPermanent = dto.getIngredients().stream()
                .filter(i -> !i.isPermanent())
                .toList();
        product.setCustomizations(mapCustomizations(product, ingredients, nonPermanent));

        // Combinations
        Set<Product> combinations = new HashSet<>(productRepository.findAllById(dto.getCombinableProducts()));
        product.setCombinations(combinations);
    }
    private ProductToDisplay toProductToDisplay(Product product) {
        ProductToDisplay dto = new ProductToDisplay();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setWeight(product.getGrams());
        dto.setImage(product.getImageURL());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory() != null ? product.getCategory().name() : null);
        dto.setRecipe(product.getRecipe());

        // Safely map customizations (skip null ingredients)
        List<CustomizationDto> customizationDtos = Optional.ofNullable(product.getCustomizations())
                .orElse(Collections.emptyList())
                .stream()
                .filter(c -> c != null && c.getIngredient() != null)
                .map(c -> {
                    CustomizableIngredients ing = new CustomizableIngredients();
                    ing.setId(c.getIngredient().getId());
                    ing.setName(c.getIngredient().getName());

                    CustomizationDto cdto = new CustomizationDto();
                    cdto.setId(c.getId());
                    cdto.setIngredient(ing);
                    cdto.setAddable(Boolean.TRUE.equals(c.getAddable()));
                    cdto.setRemovable(Boolean.TRUE.equals(c.getRemovable()));
                    cdto.setExtraCost(c.getExtraCost());
                    cdto.setMaxQuantity(c.getMaxQuantity());
                    return cdto;
                })
                .toList();

        dto.setCustomizations(customizationDtos);

        // Safely map recommended products (skip nulls)
        List<RecommendedProductDto> recommendedDtos = Optional.ofNullable(product.getCombinations())
                .orElse(Collections.emptySet())
                .stream()
                .filter(Objects::nonNull)
                .map(r -> {
                    RecommendedProductDto rec = new RecommendedProductDto();
                    rec.setId(r.getId());
                    rec.setName(r.getName());
                    rec.setPrice(r.getPrice());
                    rec.setImage(r.getImageURL());
                    return rec;
                })
                .toList();

        dto.setRecommended(recommendedDtos);

        return dto;
    }

    private List<ProductCustomization> mapCustomizations(Product product, Set<Ingredient> ingredients, List<IngredientDto> dtos) {
        Map<UUID, Ingredient> ingredientMap = ingredients.stream()
                .collect(Collectors.toMap(Ingredient::getId, i -> i));
        return dtos.stream().map(dto -> {
            ProductCustomization pc = new ProductCustomization();
            pc.setProduct(product);
            pc.setIngredient(ingredientMap.get(dto.getId()));
            pc.setAddable(dto.isCanBeAdded());
            pc.setRemovable(dto.isCanBeRemoved());
            pc.setExtraCost(dto.getExtraCost());
            pc.setMaxQuantity(dto.getMaxQty());
            return pc;
        }).toList();
    }

    private ProductsDto toProductsDto(Product product) {
        return ProductsDto.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory().name())
                .weight(product.getGrams())
                .price(product.getPrice())
                .image(product.getImageURL())
                .build();
    }
}
