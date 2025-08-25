package sit.tuvarna.bg.orderservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.orderservice.bill.service.BillService;
import sit.tuvarna.bg.orderservice.ingriedient.service.IngredientService;
import sit.tuvarna.bg.orderservice.onlineOrder.service.OnlineOrderService;
import sit.tuvarna.bg.orderservice.onlineOrderItem.service.OnlineOrderItemService;
import sit.tuvarna.bg.orderservice.productReview.service.ProductReviewService;
import sit.tuvarna.bg.orderservice.reveiw.service.ReviewService;
import sit.tuvarna.bg.orderservice.web.dto.heatmap.InventoryHeatMapRow;
import sit.tuvarna.bg.orderservice.web.dto.salesChart.SalesSparkDto;
import sit.tuvarna.bg.orderservice.web.dto.top5.Top5ProductsFinalDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/order_service")
//dashboard endpoints
public class FinanceController {

    private final BillService billService;
    private final OnlineOrderService  onlineOrderService;
    private final ReviewService reviewService;
    private final ProductReviewService productReviewService;
    private final OnlineOrderItemService  onlineOrderItemService;
    private final IngredientService ingredientService;

    @Autowired
    public FinanceController(BillService billService, OnlineOrderService onlineOrderService, ReviewService reviewService, ProductReviewService productReviewService, OnlineOrderItemService onlineOrderItemService, IngredientService ingredientService) {
        this.billService = billService;
        this.onlineOrderService = onlineOrderService;
        this.reviewService = reviewService;
        this.productReviewService = productReviewService;
        this.onlineOrderItemService = onlineOrderItemService;
        this.ingredientService = ingredientService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> revenueToday(){
        CompletableFuture<BigDecimal> sumFromTotals = billService.getTotalRevenueFromToday();
        CompletableFuture<BigDecimal> totalRevenueFromToday = onlineOrderService.getTotalRevenueFromToday();
        BigDecimal result = sumFromTotals.thenCombine(totalRevenueFromToday, BigDecimal::add).join();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/orders")
    public ResponseEntity<Long> ordersToday(){
        Long numberOfBillsForToday = billService.getNumberOfBillsForToday();
        Long numberOfOnlineOrdersForToday = onlineOrderService.getNumberOfOnlineOrdersForToday();
        return ResponseEntity.ok(numberOfBillsForToday + numberOfOnlineOrdersForToday);
    }

    @GetMapping("/reviews")
    public ResponseEntity<Long> newReviewsToday(){
        Long newReviews = reviewService.getNewReviews();
        Long newReviews1 = productReviewService.getNewReviews();
        return ResponseEntity.ok(newReviews + newReviews1);
    }

    @GetMapping("/top5")
    public ResponseEntity<List<Top5ProductsFinalDto>> top5(){
        List<Top5ProductsFinalDto> top5Products = onlineOrderItemService.getTop5Products();
        return ResponseEntity.ok(top5Products);
    }

    @GetMapping("/salesChart")
    public ResponseEntity<SalesSparkDto>  salesChart(){
        SalesSparkDto salesForTodayAndYesterday = billService.getSalesForTodayAndYesterday();
        return ResponseEntity.ok(salesForTodayAndYesterday);
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<InventoryHeatMapRow>> heatmap(){
        List<InventoryHeatMapRow> dataForHeatmap = ingredientService.getDataForHeatmap();
        return ResponseEntity.ok(dataForHeatmap);
    }
}
