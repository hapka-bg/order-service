package sit.tuvarna.bg.orderservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.orderservice.bill.service.BillService;
import sit.tuvarna.bg.orderservice.onlineOrder.service.OnlineOrderService;
import sit.tuvarna.bg.orderservice.product.service.ProductService;
import sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.ProductExtremesProjection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/sales_analytics")
public class SalesAndAnalyticsController {

    private final BillService billService;
    private final ProductService productService;
    private final OnlineOrderService onlineOrderService;

    @Autowired
    public SalesAndAnalyticsController(BillService billService, ProductService productService, OnlineOrderService onlineOrderService) {
        this.billService = billService;
        this.productService = productService;
        this.onlineOrderService = onlineOrderService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getRevenue(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        BigDecimal weeklyRevenue = billService.getWeeklyRevenue(start, end);
        return ResponseEntity.ok(weeklyRevenue);
    }

    //most ordered meal
    @GetMapping("/most_least")
    public ResponseEntity<ProductExtremesProjection> getMostOrdered(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        ProductExtremesProjection mostAndLeastOrdered = productService.getMostAndLeastOrdered(start, end);
        return ResponseEntity.ok(mostAndLeastOrdered);
    }

    @GetMapping("/avg")
    public ResponseEntity<BigDecimal> getAvg(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(billService.getAverageOrderValue(start,end));
    }
    @GetMapping("/sales-chart")
    public ResponseEntity<?> getSalesChart(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                                           @RequestParam String grouping) {
     return ResponseEntity.ok(onlineOrderService.getRevenueForChart(start,end,grouping));
    }
}
