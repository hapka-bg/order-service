package sit.tuvarna.bg.orderservice.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.tuvarna.bg.orderservice.auth.client.AuthClient;
import sit.tuvarna.bg.orderservice.web.dto.onlineOrders.UserDetailsForOnlineOrders;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {
    private final AuthClient authClient;

    @Autowired
    public AuthService(AuthClient authClient) {
        this.authClient = authClient;
    }


    public List<String> getNames(List<UUID> ids){
        try{
            ResponseEntity<List<String>> allStaffNames = authClient.getAllStaffNames(ids);
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
            ResponseEntity<UserDetailsForOnlineOrders> phoneNumberAndAddress = authClient.getPhoneNumberAndAddress(id);
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
}
