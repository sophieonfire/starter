package de.nrw.it.ignrw.handler;

import de.nrw.it.ignrw.context.IGNRWContextHolder;
import de.nrw.it.ignrw.property.RequestContextProperties;
import de.nrw.it.ignrw.request.header.IGNRWContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RequestContextInterceptor implements HandlerInterceptor {

    private final RequestContextProperties properties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String orgHeaderName = properties.getOrgHeader();
        String org = request.getHeader(orgHeaderName);

        if (org != null && !org.isBlank()) {
            try {
                UUID orgUuid = UUID.fromString(org.trim());
                IGNRWContext context = new IGNRWContext();
                context.setOrgUuid(orgUuid);

                // Kontext global für diesen Thread ablegen
                IGNRWContextHolder.setContext(context);
            } catch (IllegalArgumentException e) {
                // Optional: Hier direkt eine Exception werfen für globalen Handler
                throw new IllegalArgumentException("Ungültige UUID im Header " + orgHeaderName, e);
            }
        }
        return true;
    }
}
