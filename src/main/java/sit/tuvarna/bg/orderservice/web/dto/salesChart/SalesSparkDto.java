package sit.tuvarna.bg.orderservice.web.dto.salesChart;

import java.util.List;

public record SalesSparkDto(List<Long> yesterdayCounts,List<Long> todayCounts) {
}
