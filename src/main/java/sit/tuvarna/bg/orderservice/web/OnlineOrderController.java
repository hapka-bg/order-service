package sit.tuvarna.bg.orderservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.tuvarna.bg.orderservice.onlineOrder.service.OnlineOrderService;
import sit.tuvarna.bg.orderservice.onlineOrderReview.service.OnlineOrderReviewService;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.*;
import sit.tuvarna.bg.orderservice.web.dto.orderRequest.OrderRequestDTO;
import sit.tuvarna.bg.orderservice.web.dto.orderRequest.OrderResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OnlineOrderController {

    private final OnlineOrderService onlineOrderService;
    private final OnlineOrderReviewService onlineOrderReviewService;

    public OnlineOrderController(OnlineOrderService onlineOrderService, OnlineOrderReviewService onlineOrderReviewService) {
        this.onlineOrderService = onlineOrderService;
        this.onlineOrderReviewService = onlineOrderReviewService;
    }


    @GetMapping("/ongoing")
    public ResponseEntity<List<OngoingOrdersDisplay>> getOngoingOrders() {
        List<OngoingOrdersDisplay> ongoingOrders = onlineOrderService.getOngoingOrders();
        return ResponseEntity.ok(ongoingOrders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OngoingOrderDetails> getOngoingOrderDetails(@PathVariable UUID orderId, @RequestParam UUID userId) {
        OngoingOrderDetails ongoingOrderDetails = onlineOrderService.getOngoingOrderDetails(orderId, userId);
        return ResponseEntity.ok(ongoingOrderDetails);
    }

    @GetMapping("/period")
    public ResponseEntity<List<FilteredOnlineOrders>> getOrdersByPeriod(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<FilteredOnlineOrders> ordersBetween = onlineOrderService.getOrdersBetween(start, end);
        return ResponseEntity.ok(ordersBetween);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<OnlineOrderReviewsDisplay>> getReviews(
            @RequestParam("from") LocalDateTime from,
            @RequestParam("to") LocalDateTime to) {
        List<OnlineOrderReviewsDisplay> reviews = onlineOrderReviewService.getReviews(from, to);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/delivery-vs-satisfaction")
    public ResponseEntity<List<DeliveryVsSatisfactionDTO>> getDataForDeliveryVsSatisfaction() {
        List<DeliveryVsSatisfactionDTO> dataForSatisfaction = onlineOrderReviewService.getDataForSatisfaction();
        return ResponseEntity.ok(dataForSatisfaction);
    }

    @GetMapping("/orders-per-day")
    public ResponseEntity<List<Long>> getOrdersPerDay() {
        List<Long> ordersPerDay = onlineOrderService.getOrdersPerDay();
        return ResponseEntity.ok(ordersPerDay);
    }

    @PostMapping("/place")
    public ResponseEntity<OrderResponseDTO> postOrder(@RequestBody OrderRequestDTO orderRequest,
                                                      @RequestHeader("Authorization") String authHeader) {
        OrderResponseDTO orderResponseDTO = onlineOrderService.processOrder(orderRequest, authHeader);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OnlineOrdersResponseForUser>> getAllOrdersForUser(@RequestHeader("Authorization") String authHeader) {
        List<OnlineOrdersResponseForUser> allOrdersForUser = onlineOrderService.getAllOrdersForUser(authHeader);
        return ResponseEntity.ok(allOrdersForUser);
    }


    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> getOrderItems(@PathVariable UUID orderId,
                                                                 @RequestHeader("Authorization") String authHeader) {
        List<OrderItemResponse> items = onlineOrderService.getOrderItemsForUser(orderId,authHeader);
        return ResponseEntity.ok(items);

    }
}
