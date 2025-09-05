package sit.tuvarna.bg.orderservice.bill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.auth.service.AuthService;
import sit.tuvarna.bg.orderservice.bill.repository.BillRepository;
import sit.tuvarna.bg.orderservice.onlineOrder.service.OnlineOrderService;
import sit.tuvarna.bg.orderservice.web.dto.analytics.OrdersPerDay;
import sit.tuvarna.bg.orderservice.web.dto.analytics.WaiterOrders;
import sit.tuvarna.bg.orderservice.web.dto.salesAndAnalytics.SumCount;
import sit.tuvarna.bg.orderservice.web.dto.salesChart.HourlyCountDto;
import sit.tuvarna.bg.orderservice.web.dto.salesChart.SalesSparkDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final AuthService authService;
    private final OnlineOrderService onlineOrderService;

    @Autowired
    public BillService(BillRepository billRepository, AuthService authService, OnlineOrderService onlineOrderService) {
        this.billRepository = billRepository;
        this.authService = authService;
        this.onlineOrderService = onlineOrderService;
    }

    @Async
    public CompletableFuture<BigDecimal> getTotalRevenueFromToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        return CompletableFuture.supplyAsync(()->billRepository.sumFromTotal(startOfDay, endOfDay));
    }

    public BigDecimal getWeeklyRevenue(LocalDateTime start, LocalDateTime end) {
        BigDecimal billsRevenue = billRepository.sumFromTotal(start, end);
        BigDecimal ordersRevenue = onlineOrderService.sumFromTotal(start, end);
        return billsRevenue.add(ordersRevenue);
    }

    public Long getNumberOfBillsForToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        return billRepository.countBillByIssuedAtBetween(startOfDay, endOfDay);
    }

    public SalesSparkDto getSalesForTodayAndYesterday() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        List<HourlyCountDto> billsToday = billRepository.countByHour(today);
        List<HourlyCountDto> billsYesterday = billRepository.countByHour(yesterday);
        List<HourlyCountDto> ordersToday = onlineOrderService.countByHour(today);
        List<HourlyCountDto> ordersYesterday = onlineOrderService.countByHour(yesterday);
        List<Integer> hours = IntStream.rangeClosed(8, 19).boxed().toList();
        Map<Integer, Long> todayMap = mergeCounts(billsToday, ordersToday);
        Map<Integer, Long> yesterdayMap = mergeCounts(billsYesterday, ordersYesterday);
        List<Long> todayCounts = hours.stream()
                .map(h -> todayMap.getOrDefault(h, 0L))
                .toList();

        List<Long> yesterdayCounts = hours.stream()
                .map(h -> yesterdayMap.getOrDefault(h, 0L)).toList();

        return new SalesSparkDto(yesterdayCounts, todayCounts);
    }

    public BigDecimal getAverageOrderValue(LocalDateTime start, LocalDateTime end) {
        // Defensive: ensure a valid range
        if (start == null || end == null || !end.isAfter(start)) {
            return BigDecimal.ZERO;
        }

        SumCount online = onlineOrderService.sumAndCountOnlineOrders(start, end);
        SumCount dineIn = billRepository.sumAndCountBills(start, end);

        BigDecimal totalSum = online.getSum().add(dineIn.getSum());
        long totalCount = online.getCount() + dineIn.getCount();

        if (totalCount == 0) return BigDecimal.ZERO;

        return totalSum.divide(BigDecimal.valueOf(totalCount), RoundingMode.HALF_UP);
    }

    public List<OrdersPerDay> getAllOrdersPerDay() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<OrdersPerDay> online = onlineOrderService.findOnlineOrdersPerDay(sevenDaysAgo);

        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        List<OrdersPerDay> bills = billRepository.findBillsPerDay(fromDate);

        Map<LocalDate, Long> merged = new TreeMap<>();
        Stream.concat(online.stream(), bills.stream())
                .forEach(dto -> merged.merge(dto.getOrderDay(), dto.getOrderCount(), Long::sum));

        return merged.entrySet().stream()
                .map(e -> new OrdersPerDay(e.getKey(), e.getValue()))
                .toList();
    }

    public List<WaiterOrders> getOrdersPerWaiter() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(7);
        List<WaiterOrders> waiterOrders = billRepository.countOrdersByWaiter(start, end);
        List<UUID> ids = waiterOrders.stream()
                .map(WaiterOrders::getWaiterId)
                .toList();
        List<String> names = authService.getNames(ids);
        int index = 0;
        for (WaiterOrders waiterOrder : waiterOrders) {
            waiterOrder.setName(names.get(index++));
            if (index == names.size()) break;
        }
        return waiterOrders;
    }

    private Map<Integer, Long> mergeCounts(List<HourlyCountDto> a, List<HourlyCountDto> b) {
        Map<Integer, Long> map = new HashMap<>();
        Stream.concat(a.stream(), b.stream()).forEach(dto ->
                map.merge(dto.hour(), dto.count(), Long::sum)
        );
        return map;
    }
}
