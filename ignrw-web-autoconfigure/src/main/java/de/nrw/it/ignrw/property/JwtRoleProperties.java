package de.nrw.it.ignrw.property;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ignrw.security.jwt")
public class JwtRoleProperties {

    @NotEmpty
    private String claim = "sub";

    @NotEmpty
    private String rolePrefix = "ROLE_";

    private boolean includeJwtScopes = true;
}
