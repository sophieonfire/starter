package de.nrw.it.ignrw.autoconfig;

import de.nrw.it.ignrw.property.JwtRoleProperties;
import de.nrw.it.ignrw.property.RequestContextProperties;
import de.nrw.it.ignrw.repository.UserRoleRepo;
import de.nrw.it.ignrw.service.PermissionService;
import de.nrw.it.ignrw.service.UserPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@AutoConfiguration
@EnableConfigurationProperties(JwtRoleProperties.class)
@MapperScan(basePackageClasses = de.nrw.it.ignrw.repository.UserRoleRepo.class)
public class AuthorityAutoConfiguration {

    private static final String CONTEXT_ORG_UUID = "contextOrgUuid";

    @Bean
//    @ConditionalOnClass(SqlSessionFactory.class)
//    @ConditionalOnMissingBean(UserRoleService.class)
    @ConditionalOnBean(UserRoleRepo.class) // wichtig: nur wenn Repo-Bean existiert
    public PermissionService userRoleService(UserRoleRepo mapper) {
        return new UserPermissionService(mapper);
    }

    @Bean(name = "ignrwJwtAuthenticationConverter")
    @ConditionalOnBean(PermissionService.class)
    @ConditionalOnMissingBean(name = "ignrwJwtAuthenticationConverter")
    public Converter<Jwt, AbstractAuthenticationToken> ignrwJwtAuthenticationConverter(PermissionService permissionService, JwtRoleProperties properties, RequestContextProperties contextProperties) {
        return jwt -> {
            JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> authorities = new HashSet<>();

            if (properties.isIncludeJwtScopes()) {
                authorities.addAll(scopeConverter.convert(jwt));
            }

            String keycloakId = jwt.getClaimAsString(properties.getClaim());

            HttpServletRequest request = null;
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            String orgUuid = null;
            if (sra != null) {
                request = sra.getRequest();
                String orgHeader = contextProperties.getOrgHeader();
                String headerContext = request.getHeader(orgHeader);

                if (headerContext != null && !headerContext.isEmpty()) {
                    orgUuid = headerContext;
                } else {
                    orgUuid = request.getParameter(CONTEXT_ORG_UUID);
                }
            }

            if (keycloakId != null && orgUuid != null) {
                permissionService.getUserRolesByKeycloakId(keycloakId, UUID.fromString(orgUuid))
                        .forEach(role -> authorities.add(new SimpleGrantedAuthority(properties.getRolePrefix() + role)));
            }

            String username = Optional.ofNullable(jwt.getClaimAsString("preferred_username"))
                    .orElse(jwt.getSubject());

            return new JwtAuthenticationToken(jwt, authorities, username);
        };
    }
}

