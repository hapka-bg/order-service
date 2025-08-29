package sit.tuvarna.bg.orderservice.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.tuvarna.bg.orderservice.auth.client.AuthClient;
import sit.tuvarna.bg.orderservice.auth.client.StaffClient;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.UserDetailsForOnlineOrders;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {
    private final StaffClient staffClient;
    private final AuthClient authClient;

    @Autowired
    public AuthService(StaffClient staffClient, AuthClient authClient) {
        this.staffClient = staffClient;
        this.authClient = authClient;
    }


    public List<String> getNames(List<UUID> ids){
        try{
            ResponseEntity<List<String>> allStaffNames = staffClient.getAllStaffNames(ids);
            if (!allStaffNames.getStatusCode().is2xxSuccessful()) {
                log.error("[Feign call to auth-svc failed] Couldn't get names");
            }
            return allStaffNames.getBody();
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public UserDetailsForOnlineOrders getPhoneNumberAndAddress(UUID id){
        try{
            ResponseEntity<UserDetailsForOnlineOrders> phoneNumberAndAddress = staffClient.getPhoneNumberAndAddress(id);
            if(!phoneNumberAndAddress.getStatusCode().is2xxSuccessful()){
                log.error("[Feign call to auth-svc failed] Couldn't get phone number and address");
            }
            return phoneNumberAndAddress.getBody();
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    public UUID extractUserId(String authHeader) {
        try {
            ResponseEntity<UUID> response = authClient.extractUserId(authHeader);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("[Feign call to auth-svc failed] Couldn't extract userId from JWT");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("[Feign call to auth-svc failed] {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
