package sit.tuvarna.bg.orderservice.onlineOrderItem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.onlineOrderItem.repository.OnlineOrderItemRepository;
import sit.tuvarna.bg.orderservice.product.module.Product;
import sit.tuvarna.bg.orderservice.product.service.ProductService;
import sit.tuvarna.bg.orderservice.web.dto.top5.Top5ProductsFinalDto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

@Service
public class OnlineOrderItemService {


    private final OnlineOrderItemRepository onlineOrderItemRepository;
    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @Autowired
    public OnlineOrderItemService(OnlineOrderItemRepository repository, ProductService productService) {
        this.onlineOrderItemRepository = repository;
        this.productService = productService;
        this.objectMapper = new ObjectMapper();
    }


    public List<Top5ProductsFinalDto> getTop5Products() {
        return onlineOrderItemRepository.fetchProductSparks().stream()
                .map(p->{
                    UUID productId = UUID.fromString(p.getProductId());
                    List<Integer> spark = parseJsonArray(p.getUnitsArray());
                    Product product = productService.getById(productId);
                    int totalUnits = spark.stream().mapToInt(Integer::intValue).sum();

                    return Top5ProductsFinalDto.builder()
                            .name(product.getName())
                            .units(totalUnits)
                            .imageURL(product.getImageURL())
                            .spark(spark.toArray(new Integer[0]))
                            .build();
                }).toList();
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
