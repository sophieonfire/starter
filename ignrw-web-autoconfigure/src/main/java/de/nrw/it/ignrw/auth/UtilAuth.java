package de.nrw.it.ignrw.auth;

import de.nrw.it.ignrw.datatype.RoleId;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class UtilAuth {

    public Set<RoleId> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .map(a -> a.replaceFirst("^ROLE_", "")).map(name -> {
                    try {
                        return RoleId.valueOf(name);
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
