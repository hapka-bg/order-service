package sit.tuvarna.bg.orderservice.web.dto.orderRequest;

import lombok.Data;

@Data
public class ExtrasDTO {
    private boolean cutleryNapkins;
    private boolean sushiSticks;
    private String sushiSticksCount;
}
