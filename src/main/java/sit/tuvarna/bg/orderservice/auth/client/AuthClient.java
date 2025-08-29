package sit.tuvarna.bg.orderservice.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "auth-service-auth", url = "http://localhost:8082/api/auth")
public interface AuthClient {
    @GetMapping("/extract-user-id")
    ResponseEntity<UUID> extractUserId(@RequestHeader("Authorization") String authHeader);
}
