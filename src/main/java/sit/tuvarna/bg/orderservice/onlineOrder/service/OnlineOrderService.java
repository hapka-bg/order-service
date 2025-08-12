package sit.tuvarna.bg.orderservice.onlineOrder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.onlineOrder.repository.OnlineOrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class OnlineOrderService {

    private final OnlineOrderRepository onlineOrderRepository;

    @Autowired
    public OnlineOrderService(OnlineOrderRepository onlineOrderRepository) {
        this.onlineOrderRepository = onlineOrderRepository;
    }

    @Async
    public CompletableFuture<BigDecimal> getTotalRevenueFromToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        return CompletableFuture.completedFuture(onlineOrderRepository.sumFromTotal(startOfDay, endOfDay));
    }
}
