package sit.tuvarna.bg.orderservice.web.dto.users;


import lombok.Data;

import java.util.UUID;

@Data
public class CustomizableIngredients {
    private UUID id;
    private String name;
}
