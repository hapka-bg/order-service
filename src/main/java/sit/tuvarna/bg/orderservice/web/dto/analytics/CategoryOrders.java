package sit.tuvarna.bg.orderservice.web.dto.analytics;

import sit.tuvarna.bg.orderservice.product.module.Category;

public record CategoryOrders(Category categoryName, Long ordersCount) {}