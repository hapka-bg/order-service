package sit.tuvarna.bg.orderservice.web.dto.top5;

import java.util.List;
import java.util.UUID;

public record ProductSparkDto(UUID product_id, List<Integer> spark) {
}
