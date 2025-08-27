package sit.tuvarna.bg.orderservice.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.auth.service.AuthService;
import sit.tuvarna.bg.orderservice.order.repository.OrderRepository;
import sit.tuvarna.bg.orderservice.web.dto.analytics.CategoryOrders;
import sit.tuvarna.bg.orderservice.web.dto.orders.OrderSummary;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AuthService authClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, AuthService authClient) {
        this.orderRepository = orderRepository;
        this.authClient = authClient;
    }

    public List<OrderSummary> getAllOrders() {
        List<OrderSummary> orderSummaries = orderRepository.findOrderSummaries(LocalDateTime.now().minusMonths(1));
        List<UUID> ids = orderSummaries.stream()
                .map(OrderSummary::getUserId)
                .toList();
        List<String> names = authClient.getNames(ids);
        int index=0;
        for (OrderSummary orderSummary : orderSummaries) {
            orderSummary.setName(names.get(index++));
            if(index==names.size()){
                break;
            }
        }
        return orderSummaries.stream().sorted(Comparator.comparing(OrderSummary::getOrderDate).reversed()).toList();
    }

    public List<CategoryOrders> getPopularCategories(){
        return orderRepository.findPopularCategories();
    }
}
