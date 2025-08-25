package sit.tuvarna.bg.orderservice.utils;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
//so it send the jwt when making a feign call to auth service
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attrs  = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attrs!=null){
            HttpServletRequest request = attrs.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                requestTemplate.header("Authorization", authHeader);
            }
        }
    }
}
