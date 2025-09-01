package sit.tuvarna.bg.orderservice.onlineOrder.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.tuvarna.bg.orderservice.auth.service.AuthService;
import sit.tuvarna.bg.orderservice.ingriedient.model.Ingredient;
import sit.tuvarna.bg.orderservice.ingriedient.service.IngredientService;
import sit.tuvarna.bg.orderservice.onlineOrder.model.DeliveryMethod;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrder;
import sit.tuvarna.bg.orderservice.onlineOrder.model.OnlineOrderStatus;
import sit.tuvarna.bg.orderservice.onlineOrder.model.PaymentMethod;
import sit.tuvarna.bg.orderservice.onlineOrder.repository.OnlineOrderRepository;
import sit.tuvarna.bg.orderservice.onlineOrderCustomization.model.OnlineOrderCustomization;
import sit.tuvarna.bg.orderservice.onlineOrderCustomization.model.Type;
import sit.tuvarna.bg.orderservice.onlineOrderItem.model.OnlineOrderItem;
import sit.tuvarna.bg.orderservice.product.module.Product;
import sit.tuvarna.bg.orderservice.product.service.ProductService;
import sit.tuvarna.bg.orderservice.productCustomization.module.ProductCustomization;
import sit.tuvarna.bg.orderservice.promoCodes.service.PromoCodeService;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.*;
import sit.tuvarna.bg.orderservice.web.dto.orderRequest.*;
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
    private final ProductService productService;
    private final IngredientService ingredientService;
    private final PromoCodeService promoCodeService;



    @Autowired
    public OnlineOrderService(OnlineOrderRepository onlineOrderRepository, AuthService authService, ProductService productService, IngredientService ingredientService, PromoCodeService promoCodeService) {
        this.onlineOrderRepository = onlineOrderRepository;
        this.authService = authService;
        this.productService = productService;
        this.ingredientService = ingredientService;
        this.promoCodeService = promoCodeService;
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

    @Transactional
    public OrderResponseDTO processOrder(OrderRequestDTO dto, String authHeader) {
        UUID userId = authService.extractUserId(authHeader);
        // 1. Create the order entity
            OnlineOrder order = new OnlineOrder();
            order.setUserId(userId);
            order.setOnlineOrderStatus(OnlineOrderStatus.PLACED);
            order.setDeliveryMethod(DeliveryMethod.DELIVERY);
            order.setPaymentMethod(PaymentMethod.CASH);

            BigDecimal subtotal = BigDecimal.ZERO;

            // 2. Map products to order items
            for (ProductDTO pDto : dto.getProducts()) {
                Product product = productService.getById(pDto.getId());
                OnlineOrderItem item = new OnlineOrderItem();
                item.setOrder(order);
                item.setProduct(product);
                item.setQuantity(pDto.getQuantity());
                item.setUnitPrice(BigDecimal.valueOf(pDto.getPrice()));

                ProductCustomizationsDTO customizations = pDto.getCustomizations();
                // Map customizations
                if (!customizations.getAdded().isEmpty() ) {
                    // Added
                    for (ProductCustomizationDTO add : customizations.getAdded()) {
                        Ingredient ingredient = ingredientService.getById(add.getId());
                        OnlineOrderCustomization customization = new OnlineOrderCustomization();
                        customization.setOrderItem(item);
                        customization.setIngredient(ingredient);
                        customization.setType(Type.ADD);
                        customization.setQuantity(add.getQuantity());
                        BigDecimal extraCost = findExtraCostForIngredient(ingredient, product.getCustomizations());
                        customization.setExtraCost(extraCost);

                        item.getCustomizations().add(customization);
                    }
                }
                if (!customizations.getRemoved().isEmpty()) {
                    // Removed
                    for (ProductCustomizationDTO rem : customizations.getRemoved()) {
                        Ingredient ingredient = ingredientService.getById(rem.getId());
                        OnlineOrderCustomization customization = new OnlineOrderCustomization();
                        customization.setOrderItem(item);
                        customization.setIngredient(ingredient);
                        customization.setType(Type.REMOVE);
                        customization.setQuantity(1);
                        customization.setExtraCost(BigDecimal.ZERO);

                        item.getCustomizations().add(customization);
                    }
                }

                order.getItems().add(item);

                // Add to subtotal
                subtotal = subtotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }

            // 3. Apply delivery fee
            BigDecimal backendTotal = subtotal.add(BigDecimal.valueOf(5.00)); // baseDeliveryFee

            // 4. Apply extras
            if (dto.getExtras() != null) {
                if (dto.getExtras().isCutleryNapkins()) {
                    backendTotal = backendTotal.add(BigDecimal.valueOf(1.00));
                }
                if (dto.getExtras().isSushiSticks() && dto.getExtras().getSushiSticksCount() != null) {
                    try {
                        int count = Integer.parseInt(dto.getExtras().getSushiSticksCount());
                        backendTotal = backendTotal.add(BigDecimal.valueOf(count * 0.10));
                    } catch (NumberFormatException ignored) {}
                }
            }

//             5. Promo code logic
             BigDecimal discount = BigDecimal.ZERO;
             if (dto.getPromoCode() != null && !dto.getPromoCode().isBlank()) {
                 discount = promoCodeService.calculateDiscount(dto.getPromoCode(), backendTotal,userId);
                 backendTotal = backendTotal.subtract(discount);
             }

            // 6. Compare backend total with frontend total
            BigDecimal frontendTotal = BigDecimal.valueOf(dto.getTotal());
            if (backendTotal.compareTo(frontendTotal) != 0) {
                throw new IllegalArgumentException("Total mismatch: frontend=" + frontendTotal + ", backend=" + backendTotal);
            }

            // 7. Set totals in order
            order.setTotal(subtotal);
            order.setDiscount(discount);
            order.setTax(BigDecimal.ZERO);
            order.setFinalTotal(backendTotal);

            // 8. Save order
            OnlineOrder saved = onlineOrderRepository.save(order);

            checkForPromoCode(userId);
            // 9. Return response
            return new OrderResponseDTO(saved.getId(), saved.getOnlineOrderStatus(), saved.getFinalTotal());

    }

    public List<OnlineOrdersResponseForUser> getAllOrdersForUser(String authHeader) {
        UUID userId = authService.extractUserId(authHeader);
        List<OnlineOrder> allByUserIdOrderByCreatedAtDesc = onlineOrderRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        List<OnlineOrdersResponseForUser> response = new ArrayList<>();
        for (OnlineOrder order : allByUserIdOrderByCreatedAtDesc) {
            OnlineOrdersResponseForUser build = OnlineOrdersResponseForUser.builder()
                    .orderId(order.getId())
                    .createdAt(order.getCreatedAt())
                    .itemsCount(order.getItems().size())
                    .status(order.getOnlineOrderStatus().name())
                    .total(order.getFinalTotal())
                    .build();
            response.add(build);
        }
        return response;

    }

    public List<OrderItemResponse> getOrderItemsForUser(UUID orderId, String authHeader) {
        OnlineOrder onlineOrder = onlineOrderRepository.findById(orderId).orElseThrow(() ->new EntityNotFoundException("Order not found"));
        UUID userId = authService.extractUserId(authHeader);
        if (!onlineOrder.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to view this order");
        }
        List<OnlineOrderItem> items = onlineOrder.getItems();
        List<OrderItemResponse> result = new ArrayList<>();
        for (OnlineOrderItem item : items) {
            OrderItemResponse build = OrderItemResponse.builder()
                    .name(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .build();
            result.add(build);
        }

        return result;
    }

    private void checkForPromoCode(UUID userId) {
        long orderCount = onlineOrderRepository.countByUserId(userId);
        if (orderCount % 5 == 0 && orderCount > 0) {
            promoCodeService.initPromoCodeForUser(userId);
        }
    }

    private BigDecimal findExtraCostForIngredient(Ingredient ingredient, List<ProductCustomization> customizations) {
        for (ProductCustomization customization : customizations) {
            Ingredient ingredient1 = customization.getIngredient();
            if(ingredient1.equals(ingredient)) {
                return customization.getExtraCost();
            }
        }
        return BigDecimal.ZERO;
    }


}
