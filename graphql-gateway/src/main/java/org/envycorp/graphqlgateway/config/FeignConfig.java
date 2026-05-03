package org.envycorp.graphqlgateway.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor jwtForwardingInterceptor() {
        return new JwtForwardingInterceptor();
    }

    static class JwtForwardingInterceptor implements RequestInterceptor {

        private static final String AUTHORIZATION_HEADER = "Authorization";
        private static final String BEARER_PREFIX = "Bearer ";

        @Override
        public void apply(RequestTemplate template) {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
                template.header(AUTHORIZATION_HEADER, authorizationHeader);
                log.debug("Forwarding JWT to: {}", template.url());
            }
        }
    }
}
