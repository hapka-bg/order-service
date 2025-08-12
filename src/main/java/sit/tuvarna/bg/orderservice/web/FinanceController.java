package sit.tuvarna.bg.orderservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.orderservice.bill.service.BillService;
import sit.tuvarna.bg.orderservice.onlineOrder.service.OnlineOrderService;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/order_service")
public class FinanceController {

    private final BillService billService;
    private final OnlineOrderService  onlineOrderService;

    @Autowired
    public FinanceController(BillService billService, OnlineOrderService onlineOrderService) {
        this.billService = billService;
        this.onlineOrderService = onlineOrderService;
    }

    @GetMapping("/revenue")
    public CompletableFuture<BigDecimal> revenueToday(){
        CompletableFuture<BigDecimal> sumFromTotals = billService.getTotalRevenueFromToday();
        CompletableFuture<BigDecimal> totalRevenueFromToday = onlineOrderService.getTotalRevenueFromToday();
        return sumFromTotals.thenCombine(totalRevenueFromToday, BigDecimal::add);
    }
}
