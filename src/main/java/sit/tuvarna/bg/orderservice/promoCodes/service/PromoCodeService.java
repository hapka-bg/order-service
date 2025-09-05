package sit.tuvarna.bg.orderservice.promoCodes.service;

import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.auth.service.AuthService;
import sit.tuvarna.bg.orderservice.exceptions.InvalidPromoCodeException;
import sit.tuvarna.bg.orderservice.exceptions.MissingPromoCodeException;
import sit.tuvarna.bg.orderservice.promoCodes.model.PromoCode;
import sit.tuvarna.bg.orderservice.promoCodes.repository.PromoCodeRepository;
import sit.tuvarna.bg.orderservice.web.dto.promoCodes.PromoCodesResponse;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PromoCodeService {
    private static final String ALPHANUM="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom  RANDOM = new SecureRandom();
    private static final Integer CODELENGHT=7;


    private final PromoCodeRepository promoCodeRepository;
    private final AuthService authService;

    public PromoCodeService(PromoCodeRepository promoCodeRepository, AuthService authService) {
        this.promoCodeRepository = promoCodeRepository;
        this.authService = authService;
    }

    public void initPromoCodeForUser(UUID userId) {
        String code= generateCode();
        PromoCode voucher = PromoCode.builder()
                .code(code)
                .minOrderPrice(BigDecimal.valueOf(10.00))
                .discountAmount(BigDecimal.valueOf(5.00))
                .deadline(LocalDateTime.now().plusDays(30))
                .userId(userId)
                .build();
        promoCodeRepository.save(voucher);
    }


    public BigDecimal calculateDiscount(String code, BigDecimal backendTotal, UUID userId) {
        PromoCode promoCode = promoCodeRepository.findByCode(code).orElseThrow(() -> new MissingPromoCodeException("promoCode not found"));
        if (promoCode.getDeadline().isBefore(LocalDateTime.now())) {
            throw new InvalidPromoCodeException("Promo code expired");
        }
        if (backendTotal.compareTo(promoCode.getMinOrderPrice()) < 0) {
            throw new InvalidPromoCodeException("Order total must be at least " + promoCode.getMinOrderPrice());
        }
        if (promoCode.getUserId() != null && !promoCode.getUserId().equals(userId)) {
            throw new InvalidPromoCodeException("Promo code not valid for this user");
        }
        BigDecimal discount  = promoCode.getDiscountAmount();
        if (discount.compareTo(backendTotal) > 0) {
            discount = backendTotal;
        }
        return discount;
    }

    public List<PromoCodesResponse> getAllPromoCodesForUser(String authHeader) {
        UUID uuid = authService.extractUserId(authHeader);
        return promoCodeRepository.findAllByUserId(uuid)
                .stream()
                .map(promoCode -> PromoCodesResponse.builder()
                        .code(promoCode.getCode())
                        .deadline(promoCode.getDeadline())
                        .minPurchase(promoCode.getMinOrderPrice())
                        .discount(promoCode.getDiscountAmount())
                        .build()).toList();
    }


    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODELENGHT);
        for (int i = 0; i < CODELENGHT; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}
