package sit.tuvarna.bg.orderservice.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.product.module.Product;
import sit.tuvarna.bg.orderservice.product.repository.ProductRepository;
import sit.tuvarna.bg.orderservice.web.dto.products.CustomProduct;
import sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.ProductExtremesProjection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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

}
