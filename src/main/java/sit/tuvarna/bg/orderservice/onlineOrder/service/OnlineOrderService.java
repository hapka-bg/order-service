package sit.tuvarna.bg.orderservice.onlineOrder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.auth.service.AuthService;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrder;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrderStatus;
import sit.tuvarna.bg.orderservice.onlineOrder.repository.OnlineOrderRepository;
import sit.tuvarna.bg.orderservice.onlineOrderItem.model.OnlineOrderItem;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.*;
import sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.RevenuePoint;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class OnlineOrderService {

    private final OnlineOrderRepository onlineOrderRepository;
    private final AuthService authService;

    @Autowired
    public OnlineOrderService(OnlineOrderRepository onlineOrderRepository, AuthService authService) {
        this.onlineOrderRepository = onlineOrderRepository;
        this.authService = authService;
    }

    @Async
    public CompletableFuture<BigDecimal> getTotalRevenueFromToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        return CompletableFuture.completedFuture(onlineOrderRepository.sumFromTotal(startOfDay, endOfDay));
    }

    public Long getNumberOfOnlineOrdersForToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        return onlineOrderRepository.countOnlineOrderByCreatedAtBetween(startOfDay, endOfDay);
    }

    public List<RevenuePoint> getRevenueForChart(LocalDateTime start, LocalDateTime end, String grouping) {
        return onlineOrderRepository.findRevenueByPeriod(start, end, grouping).stream()
                .map(row -> new RevenuePoint(
                        String.valueOf(row[0]),
                        (BigDecimal) row[1]
                )).toList();
    }

    public List<OngoingOrdersDisplay> getOngoingOrders() {
        List<OnlineOrderStatus> orderStatuses = List.of(OnlineOrderStatus.PLACED, OnlineOrderStatus.SHIPPED, OnlineOrderStatus.PREPARING);
        List<OnlineOrder> ongoingOrders = onlineOrderRepository.findAllByOnlineOrderStatusInOrderByCreatedAtDesc(orderStatuses);
        List<UUID> uuids = ongoingOrders.stream()
                .map(OnlineOrder::getUserId)
                .toList();
        List<String> names = authService.getNames(uuids);
        List<OngoingOrdersDisplay> result = new ArrayList<>();
        int index = 0;
        for (OnlineOrder ongoingOrder : ongoingOrders) {
            OngoingOrdersDisplay ongoingOrdersDisplay = OngoingOrdersDisplay.builder()
                    .userId(ongoingOrder.getUserId())
                    .orderId(ongoingOrder.getId())
                    .name(names.get(index++))
                    .total(ongoingOrder.getTotal())
                    .status(ongoingOrder.getOnlineOrderStatus().name())
                    .timePlaced(ongoingOrder.getCreatedAt())
                    .build();
            result.add(ongoingOrdersDisplay);
            if (index == names.size()) break;
        }
        return result;
    }

    public OngoingOrderDetails getOngoingOrderDetails(UUID orderId, UUID userId) {
        //we need to return:
        //items in the order in the following way
        OnlineOrder onlineOrder = onlineOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Cant find order!"));
        List<OnlineOrderItem> items = onlineOrder.getItems();
        Map<String, Integer> map = new HashMap<>();
        //burger 2
        for (OnlineOrderItem item : items) {
            Integer quantity = item.getQuantity();
            String name = item.getProduct().getName();
            map.put(name, quantity);
        }
        //address and phone number for that user:
        UserDetailsForOnlineOrders phoneNumberAndAddress = authService.getPhoneNumberAndAddress(userId);

        return OngoingOrderDetails.builder()
                .phoneNumber(phoneNumberAndAddress.getPhoneNumber())
                .address(phoneNumberAndAddress.getAddress())
                .map(map)
                .build();
    }

    public List<FilteredOnlineOrders> getOrdersBetween(LocalDateTime start, LocalDateTime end) {
        List<OnlineOrder> filteredOnlineOrders = onlineOrderRepository.findAllByCreatedAtBetweenOrderByDeliveredAtDesc(start, end);
        List<UUID> list = filteredOnlineOrders.stream()
                .map(OnlineOrder::getUserId)
                .toList();
        System.out.println(list);
        List<String> names = authService.getNames(list);
        List<FilteredOnlineOrders> result = new ArrayList<>();
        int index = 0;
        for (OnlineOrder onlineOrder : filteredOnlineOrders) {
            FilteredOnlineOrders build = FilteredOnlineOrders.builder()
                    .deliveredAt(onlineOrder.getDeliveredAt())
                    .orderId(onlineOrder.getId())
                    .name(names.get(index++))
                    .total(onlineOrder.getFinalTotal())
                    .status(onlineOrder.getOnlineOrderStatus().name())
                    .build();
            result.add(build);
            if (index == names.size()) break;
        }
        return result;

    }

    public List<Long> getOrdersPerDay(){
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        return onlineOrderRepository.countOrdersPerDay(sevenDaysAgo.atStartOfDay())
                .stream()
                .map(OrdersPerDayDTO::getTotalOrders)
                .toList();


    }
}
