package de.nrw.it.ignrw.autoconfig;

import de.nrw.it.ignrw.handler.DsgvoAuthorizationManager;
import de.nrw.it.ignrw.property.HttpSecurityProperties;
import de.nrw.it.ignrw.property.ModulProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
@Slf4j
@EnableConfigurationProperties({HttpSecurityProperties.class, ModulProperties.class})
public class HttpSecurityAutoConfiguration {

    @Bean(name = "ignrwHealthChain")
    @Order(100)
    @ConditionalOnMissingBean(name = "ignrwHealthChain")
    @ConditionalOnProperty(prefix = "ignrw.security.http.health", name = "enabled", havingValue = "true", matchIfMissing = true)
    SecurityFilterChain healthChain(HttpSecurity http, HttpSecurityProperties props) throws Exception {
        log.debug("Apply 'ignrwHealthChain' for paths {}", props.getHealth().getPaths());

        http.securityMatcher(props.getHealth().getPaths().toArray(String[]::new))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        http.addFilterAfter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {

                log.debug("Security chain '{}' handling {} {}", "ignrwHealthChain", request.getMethod(),
                        request.getRequestURI());
                filterChain.doFilter(request, response);
            }
        }, SecurityContextHolderFilter.class);

        return http.build();
    }

    @Bean(name = "ignrwSwaggerChain")
    @Order(200)
    @ConditionalOnMissingBean(name = "ignrwSwaggerChain")
    @ConditionalOnProperty(prefix = "ignrw.security.http.swagger", name = "enabled", havingValue = "true", matchIfMissing = false)
    SecurityFilterChain swaggerChain(HttpSecurity http, HttpSecurityProperties props) throws Exception {
        log.debug("Apply 'ignrwSwaggerChain' for paths {}", props.getSwagger().getPaths());

        http.securityMatcher(props.getSwagger().getPaths().toArray(String[]::new))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        http.addFilterAfter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {

                log.debug("Security chain '{}' handling {} {}", "ignrwSwaggerChain", request.getMethod(),
                        request.getRequestURI());
                filterChain.doFilter(request, response);
            }
        }, SecurityContextHolderFilter.class);

        return http.build();
    }

    @Bean(name = "ignrwApiChain")
    @Order(300)
    @ConditionalOnMissingBean(name = "ignrwApiChain")
    @ConditionalOnProperty(prefix = "ignrw.security.http.api", name = "enabled", havingValue = "true", matchIfMissing = true)
    SecurityFilterChain apiChain(HttpSecurity http,
            @Qualifier("ignrwJwtAuthenticationConverter") Converter<Jwt, ? extends AbstractAuthenticationToken> converter,
            DsgvoAuthorizationManager dsgvoAuthorizationManager, HttpSecurityProperties securityProperties,
            ModulProperties modulProperties) throws Exception {

        String apiPath = "/" + modulProperties.getName() + "/api/v*/**";
        log.debug("Apply 'ignrwApiChain' for paths {}", List.of(apiPath));

        http.securityMatcher(apiPath)
                // ERZWINGT, dass die Session absolut zustandslos ist (wichtig für JWT APIs)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //.cors(cors -> cors.configurationSource(corsSource))  // explizit, optional
                .cors(Customizer.withDefaults()).csrf(csrf -> {
                    if (securityProperties.getApi().isCsrfDisabled()) {
                        csrf.disable();
                    }
                }).authorizeHttpRequests(auth -> {
                    if (securityProperties.getApi().getDsgvo().isEnabled()) {
                        auth.anyRequest().access(dsgvoAuthorizationManager);
                    } else {
                        auth.anyRequest().authenticated();
                    }
                }).oauth2ResourceServer(oauth -> oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(converter))
                        // ERZWINGT: Token darf NUR im "Authorization: Bearer"-Header gesendet werden
                        .bearerTokenResolver(new DefaultBearerTokenResolver()))
                .exceptionHandling(ex ->
                        ex.accessDeniedHandler((request, response, exception) ->
                        {
                            // Rethrow DsgvoException
                            throw exception;
                        }));

        http.addFilterAfter(new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
                apiLog(request, response, filterChain);
            }
        }, AuthorizationFilter.class);

        return http.build();
    }

    private void apiLog(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        long startTime = System.currentTimeMillis();

//        String username = resolveUsername();
        String path = request.getRequestURI();
        String method = request.getMethod();

        log.debug("Security chain '{}' handling {} {}", "ignrwApiChain", method,
                path);

        try {
            log.info("START API-Anfrage");

            filterChain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            // 3. Abschluss-Log schreiben
            if (status >= 400) {
                log.warn("END API-Anfrage -> Status: {} (Dauer: {}ms)", status, duration);
            } else {
                log.info("END API-Anfrage -> Status: {} (Dauer: {}ms)", status, duration);
            }
        }
    }

    @Bean(name = "ignrwDenyChain")
    @Order(1000)
    @ConditionalOnMissingBean(name = "ignrwDenyChain")
    SecurityFilterChain denyChain(HttpSecurity http, HttpSecurityProperties props) throws Exception {
        log.debug("Apply 'ignrwDenyChain' for paths {}", "/**");

        http.securityMatcher("/**").authorizeHttpRequests(auth -> auth.anyRequest().denyAll());

        http.addFilterAfter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {

                log.debug("Security chain '{}' handling {} {}", "ignrwDenyChain", request.getMethod(),
                        request.getRequestURI());
                filterChain.doFilter(request, response);
            }
        }, SecurityContextHolderFilter.class);

        return http.build();
    }


    @Bean
    @ConditionalOnMissingBean(name = "ignrwApiCors")
    @ConditionalOnProperty(prefix = "ignrw.security.http.api.cors", name = "enabled", havingValue = "true", matchIfMissing = true)
    public CorsConfigurationSource corsConfigurationSource(HttpSecurityProperties props) {
        log.debug("Apply 'ignrwApiCors' configuration");

        var cors = props.getApi().getCors();

        CorsConfiguration cfg = new CorsConfiguration();

        if (!cors.getAllowedOrigins().isEmpty()) {
            cfg.setAllowedOrigins(cors.getAllowedOrigins());
        }
        if (!cors.getAllowedOriginPatterns().isEmpty()) {
            cfg.setAllowedOriginPatterns(cors.getAllowedOriginPatterns());
        }
        if (!cors.getAllowedMethods().isEmpty()) {
            cfg.setAllowedMethods(cors.getAllowedMethods());
        }
        if (!cors.getAllowedHeaders().isEmpty()) {
            cfg.setAllowedHeaders(cors.getAllowedHeaders());
        }
        if (!cors.getExposedHeaders().isEmpty()) {
            cfg.setExposedHeaders(cors.getExposedHeaders());
        }
        cfg.setAllowCredentials(cors.isAllowCredentials());
        cfg.setMaxAge(cors.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        var patterns = cors.getPathPatterns().isEmpty() ? java.util.List.of("/**") : cors.getPathPatterns();

        for (String pattern : patterns) {
            source.registerCorsConfiguration(pattern, new CorsConfiguration(cfg));
        }

        return source;
    }

//    private String resolveUsername() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Jwt jwt) {
//            return jwt.getSubject();
//        }
//        return "ANONYM";
//    }
}

