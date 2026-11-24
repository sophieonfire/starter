package de.nrw.it.ignrw.property;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ignrw.security.http")
public class HttpSecurityProperties {

    @Valid
    private Health health = new Health();

    @Valid
    private Swagger swagger = new Swagger();

    @Valid
    @NotNull
    private Api api = new Api();


    @Getter
    @Setter
    public static class Health {

        private boolean enabled = true;

        private List<String> paths = List.of("/actuator/**");

    }

    @Getter
    @Setter
    public static class Swagger {

        private boolean enabled = false;

        @NotNull
        private List<String> paths = List.of("/swagger-ui/**", "/v3/api-docs/**");

//        private List<String> paths = List.of("/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/**",
//                "/swagger-resources/**", "/swagger-resources", "/v3/api-docs/**", "/proxy/**");
    }

    @Getter
    @Setter
    public static class Api {

        private boolean enabled = true;

        private boolean csrfDisabled = true;

        private Cors cors = new Cors();

        @NotNull
        @Valid
        private Dsgvo dsgvo = new Dsgvo();
    }

    @Getter
    @Setter
    public static class Cors {

        private boolean enabled = true;

        private List<String> pathPatterns = List.of("/**");

        private List<String> allowedOrigins = List.of();

        private List<String> allowedOriginPatterns = List.of();

        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

        private List<String> allowedHeaders = List.of("Authorization", "Content-Type", "Accept", "ignrw-context-org");

        private List<String> exposedHeaders = List.of();

        private boolean allowCredentials = false;

        private long maxAge = 3600L;
    }

    @Getter
    @Setter
    public static class Dsgvo {

        private boolean enabled = true;

        @NotEmpty
        private String claim = "dsgvo_accepted";
    }
}
