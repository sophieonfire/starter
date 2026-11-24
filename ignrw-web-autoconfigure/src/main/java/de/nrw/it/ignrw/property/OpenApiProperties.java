package de.nrw.it.ignrw.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ignrw.openapi")
public class OpenApiProperties {

    /**
     * Schaltet die automatische OpenAPI-Dokumentation und die Context-Header ein oder aus.
     */
    private boolean enabled = false;

    /**
     * Der Titel der API-Dokumentation. Standardmäßig wird der Name der Anwendung verwendet.
     */
    @Value("${spring.application.name}")
    private String title;

    /**
     * Die Version der API-Dokumentation.
     */
    private String version;

    /**
     * TokenUrl für die Authentifizierung
     */
    private String tokenUrl;
}
