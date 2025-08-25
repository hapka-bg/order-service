package sit.tuvarna.bg.orderservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.orderservice.bill.service.BillService;
import sit.tuvarna.bg.orderservice.order.service.OrderService;
import sit.tuvarna.bg.orderservice.web.dto.analytics.CategoryOrders;
import sit.tuvarna.bg.orderservice.web.dto.analytics.OrdersPerDay;
import sit.tuvarna.bg.orderservice.web.dto.analytics.WaiterOrders;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final BillService billService;
    private final OrderService orderService;

    @Autowired
    public AnalyticsController(BillService billService, OrderService orderService) {
        this.billService = billService;
        this.orderService = orderService;
    }

    @GetMapping("/peak-hours")
    public ResponseEntity<List<OrdersPerDay>> getPeakHours() {
        return ResponseEntity.ok(billService.getAllOrdersPerDay());
    }
    @GetMapping("/orders-per-waiter")
    public ResponseEntity<List<WaiterOrders> > getOrdersPerWaiter() {
        List<WaiterOrders> ordersPerWaiter = billService.getOrdersPerWaiter();
        return ResponseEntity.ok(ordersPerWaiter);
    }
    @GetMapping("/popular-categories")
    public ResponseEntity<List<CategoryOrders>> getPopularCategories() {
        List<CategoryOrders> popularCategories = orderService.getPopularCategories();
        return ResponseEntity.ok(popularCategories);
    }
}
