package de.nrw.it.ignrw.autoconfig;

import de.nrw.it.ignrw.property.ModulProperties;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties({ModulProperties.class})
@ConditionalOnProperty(prefix = "ignrw.modul.web", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebAutoConfiguration implements WebMvcConfigurer {

    private final ModulProperties modulProperties;
    private final ConfigurableListableBeanFactory beanFactory;

    public WebAutoConfiguration(ModulProperties modulProperties, ConfigurableListableBeanFactory beanFactory) {
        this.modulProperties = modulProperties;
        this.beanFactory = beanFactory;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        List<String> basePackages = AutoConfigurationPackages.get(beanFactory);
        List<String> subPackages = modulProperties.getWeb().getControllerPackages();

        // Berechne die finalen Packages für das Predicate
        String[] finalPackagesArray;

        if (subPackages == null || subPackages.isEmpty()) {
            // Falls keine Sub-Packages definiert sind, nimm die nackten Basis-Packages
            finalPackagesArray = basePackages.toArray(new String[0]);
        } else {
            // Kombiniere jedes Basis-Package mit jedem konfigurierten Sub-Package
            finalPackagesArray = basePackages.stream()
                    .flatMap(basePkg -> subPackages.stream()
                            .map(subPkg -> basePkg + "." + subPkg.trim()))
                    .toArray(String[]::new);
        }

        String modulName = modulProperties.getName();
        String pathPrefix = "/" + modulName + "/api/v{version}";

        configurer.addPathPrefix(pathPrefix,
                HandlerTypePredicate.forAnnotation(org.springframework.web.bind.annotation.RestController.class)
                        .and(HandlerTypePredicate.forBasePackage(finalPackagesArray)));
    }
}
