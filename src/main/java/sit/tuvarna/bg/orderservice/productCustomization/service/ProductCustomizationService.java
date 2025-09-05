package sit.tuvarna.bg.orderservice.productCustomization.service;

import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.productCustomization.module.ProductCustomization;
import sit.tuvarna.bg.orderservice.productCustomization.repository.ProductCustomizationRepository;

import java.util.List;

@Service
public class ProductCustomizationService {
    private final ProductCustomizationRepository productCustomizationRepository;


    public ProductCustomizationService(ProductCustomizationRepository productCustomizationRepository) {
        this.productCustomizationRepository = productCustomizationRepository;
    }

    public void saveAllProductCustomization(List<ProductCustomization> productCustomization) {
        productCustomizationRepository.saveAll(productCustomization);
    }
}
