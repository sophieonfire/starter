package de.nrw.it.ignrw.property;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "ignrw.request.context")
@Validated
public class RequestContextProperties {

    @NotEmpty
    private String orgHeader = "ignrw-context-org";

    @NotEmpty
    private List<String> includePatterns = new ArrayList<>(List.of("/api/**"));
}
