package de.nrw.it.ignrw.handler;

import de.nrw.it.ignrw.exception.DsgvoException;
import de.nrw.it.ignrw.property.HttpSecurityProperties;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
public class DsgvoAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final HttpSecurityProperties securityProperties;

    public DsgvoAuthorizationManager(HttpSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authentication,
            RequestAuthorizationContext context) {

        Authentication auth = authentication.get();

        if (!(auth instanceof JwtAuthenticationToken jwtAuthenticationToken) || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        Boolean dsgvoAccepted = jwtAuthenticationToken.getToken().getClaim(securityProperties.getApi().getDsgvo().getClaim());

        if (Boolean.TRUE.equals(dsgvoAccepted)) {
            return new AuthorizationDecision(true);
        }

        throw new DsgvoException("Dsgvo not accepted");
    }
}
