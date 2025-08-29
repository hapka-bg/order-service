package sit.tuvarna.bg.orderservice.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.UserDetailsForOnlineOrders;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "auth-service",url = "http://localhost:8082/api/v1/staff")
public interface StaffClient {
    @GetMapping("/names")
    ResponseEntity<List<String>> getAllStaffNames(@RequestParam List<UUID> ids);


    @GetMapping("/{id}")
    ResponseEntity<UserDetailsForOnlineOrders> getPhoneNumberAndAddress(@PathVariable UUID id);


}
