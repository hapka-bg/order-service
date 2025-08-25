package sit.tuvarna.bg.orderservice.onlineOrderItem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.onlineOrderItem.repository.OnlineOrderItemRepository;
import sit.tuvarna.bg.orderservice.product.module.Product;
import sit.tuvarna.bg.orderservice.product.repository.ProductRepository;
import sit.tuvarna.bg.orderservice.web.dto.top5.ProductSparkDto;
import sit.tuvarna.bg.orderservice.web.dto.top5.ProductSparkProjection;
import sit.tuvarna.bg.orderservice.web.dto.top5.Top5ProductsFinalDto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OnlineOrderItemService {


    private final OnlineOrderItemRepository onlineOrderItemRepository;
    private final ObjectMapper objectMapper;
    private final ProductRepository  productRepository;

    @Autowired
    public OnlineOrderItemService(OnlineOrderItemRepository repository, ProductRepository productRepository) {
        this.onlineOrderItemRepository = repository;
        this.productRepository = productRepository;
        this.objectMapper = new ObjectMapper();
    }


    public List<Top5ProductsFinalDto> getTop5Products() {
        List<ProductSparkProjection> productSparkProjections = onlineOrderItemRepository.fetchProductSparks();
        List<ProductSparkDto> list = productSparkProjections.stream()
                .map(p -> new ProductSparkDto(
                        UUID.fromString(p.getProductId()),
                        parseJsonArray(p.getUnitsArray())
                )).toList();
        //fetch data for each product and sum the spark to return it a new dto
        //for each product fetch the name and the image url
        List<Top5ProductsFinalDto> result = new ArrayList<>();
        for (ProductSparkDto productSparkDto : list) {
            UUID productId = productSparkDto.product_id();
            List<Integer> spark = productSparkDto.spark();
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found: " + productId));
            Integer sum = spark.stream().mapToInt(i -> i).sum();
            Top5ProductsFinalDto build = Top5ProductsFinalDto.builder()
                    .name(product.getName())
                    .units(sum)
                    .imageURL(product.getImageURL())
                    .spark(spark.toArray(new Integer[0]))
                    .build();
            result.add(build);
        }
        return result;
    }

    private List<Integer> parseJsonArray(String json) {
        try{
            return objectMapper.readValue(json, new TypeReference<>() {});
        }
        catch (IOException e) {
            throw new UncheckedIOException("Failed to parse unitsArray: " + json, e);
        }
    }
}
