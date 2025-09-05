package sit.tuvarna.bg.orderservice.web.dto.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {

    private String message;
    private String code;
}