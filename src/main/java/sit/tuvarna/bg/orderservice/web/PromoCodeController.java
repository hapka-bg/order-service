package sit.tuvarna.bg.orderservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.tuvarna.bg.orderservice.promoCodes.service.PromoCodeService;
import sit.tuvarna.bg.orderservice.web.dto.promoCodes.PromoCodesResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promo_codes")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @Autowired
    public PromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PromoCodesResponse>> allPromoCodes(@RequestHeader("Authorization") String authHeader) {
        List<PromoCodesResponse> allPromoCodesForUser = promoCodeService.getAllPromoCodesForUser(authHeader);
        return ResponseEntity.ok(allPromoCodesForUser);
    }
}
