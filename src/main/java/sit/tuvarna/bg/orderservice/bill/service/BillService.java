package sit.tuvarna.bg.orderservice.bill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.bill.repository.BillRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class BillService {

    private final BillRepository billRepository;

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Async
    public CompletableFuture<BigDecimal> getTotalRevenueFromToday(){
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
       return CompletableFuture.completedFuture(billRepository.sumFromTotal(startOfDay, endOfDay));
    }

}
