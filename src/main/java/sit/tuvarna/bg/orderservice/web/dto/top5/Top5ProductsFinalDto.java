package sit.tuvarna.bg.orderservice.web.dto.top5;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Top5ProductsFinalDto {
    private String name;
    private Integer units;
    private String imageURL;
    private Integer[] spark;
}
