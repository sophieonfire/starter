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
@ConfigurationProperties(prefix = "ignrw.modul")
public class ModulProperties {

    @NotEmpty
    private String name = "GIVE_ME_A_NAME";

    @Valid
    @NotNull
    private final Web web = new Web();

    @Getter
    @Setter
    public static class Web {

        private boolean enabled = true;

        private List<String> controllerPackages = List.of("controller");
    }
}
