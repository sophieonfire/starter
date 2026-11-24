package de.nrw.it.ignrw.autoconfig;

import de.nrw.it.ignrw.handler.RequestContextInterceptor;
import de.nrw.it.ignrw.property.ModulProperties;
import de.nrw.it.ignrw.property.RequestContextProperties;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties({RequestContextProperties.class, ModulProperties.class})
@RequiredArgsConstructor
public class RequestContextAutoConfiguration implements WebMvcConfigurer {

    private final RequestContextProperties requestContextProperties;
    private final ModulProperties modulProperties;

    @Bean
    public RequestContextInterceptor requestContextInterceptor() {
        return new RequestContextInterceptor(requestContextProperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(requestContextInterceptor());

        List<String> patterns = requestContextProperties.getIncludePatterns();
        if (patterns != null && !patterns.isEmpty()) {
            String prefix = "/" + modulProperties.getName();

            List<String> prefixed = patterns.stream()
                    .map(p -> prefix + p)
                    .collect(Collectors.toList());

            registration.addPathPatterns(prefixed);
        }
    }
}
