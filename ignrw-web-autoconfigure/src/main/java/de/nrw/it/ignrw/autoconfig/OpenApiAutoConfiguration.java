package de.nrw.it.ignrw.autoconfig;

import de.nrw.it.ignrw.annotation.RequiresOrganisationContext;
import de.nrw.it.ignrw.property.OpenApiProperties;
import de.nrw.it.ignrw.property.RequestContextProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;

@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@ConditionalOnProperty(prefix = "ignrw.openapi", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({OpenApiProperties.class, RequestContextProperties.class})
public class OpenApiAutoConfiguration {

    private static final Pattern ROLE_PATTERN = Pattern.compile("RoleId\\)\\.(\\w+)");

    @Bean
    @ConditionalOnMissingBean // Erlaubt der Applikation, diese Definition zu überschreiben
    public OpenAPI customOpenAPI(OpenApiProperties openApiProperties) {
        StringBuilder sbTitle = new StringBuilder();

        if (openApiProperties.getTitle() != null && !openApiProperties.getTitle().isEmpty()) {
            sbTitle.append(openApiProperties.getTitle());
            sbTitle.append(" ");
        }

        sbTitle.append("API-Documentation");

        // 1. Bearer‑Token
        SecurityScheme bearer = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .name("bearerAuth")
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Bereits vorhandenes JWT");

        // 2. Password‑Grant
        String tokenUrl = openApiProperties.getTokenUrl() != null ? openApiProperties.getTokenUrl() : "";

        OAuthFlow passwordFlow = new OAuthFlow()
                .tokenUrl(tokenUrl)
                .scopes(new Scopes().addString("openid", "OpenID‑Connect‑Scope"));

        OAuthFlows flows = new OAuthFlows().password(passwordFlow);

        SecurityScheme password = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(flows)
                .description("Keycloak‑Resource‑Owner‑Password‑Grant");


        SecurityRequirement requirement = new SecurityRequirement()
                .addList("keycloakPassword")
                .addList("bearerAuth");

        return new OpenAPI()
                .info(new Info().title(sbTitle.toString())
                        //.version(openApiProperties.getVersion())
                )
                .addSecurityItem(requirement)
                .components(new Components()
                        .addSecuritySchemes("keycloakPassword", password)
                        .addSecuritySchemes("bearerAuth", bearer));
    }

    @Bean
    @ConditionalOnMissingBean // Erlaubt der Applikation, diese Definition zu überschreiben
    public OperationCustomizer addOptionalContextFields(RequestContextProperties contextProperties) {
        return (operation, handlerMethod) -> {

            addOrganisationContextHeader(operation, handlerMethod, contextProperties);
            addRolesRolesToDescription(operation, handlerMethod);

            return operation;
        };
    }

    // Funktioniert derzeit nur mit @PreAuthorize
    private static void addRolesRolesToDescription(Operation operation, HandlerMethod handlerMethod) {
        List<String> extractedRoles = new ArrayList<>();

        PreAuthorize directAnnotation = handlerMethod.getMethodAnnotation(PreAuthorize.class);
        if (directAnnotation != null) {
            extractRolesFromExpression(directAnnotation.value(), extractedRoles);
        }

        Annotation[] annotations = handlerMethod.getMethod().getAnnotations();
        for (Annotation ann : annotations) {
            PreAuthorize metaPreAuthorize = AnnotatedElementUtils.getMergedAnnotation(ann.annotationType(), PreAuthorize.class);

            if (metaPreAuthorize != null) {
                extractRolesFromExpression(metaPreAuthorize.value(), extractedRoles);
            }
        }

        if (!extractedRoles.isEmpty()) {
            List<String> uniqueRoles = extractedRoles.stream().distinct().collect(Collectors.toList());
            String cleanedRoles = String.join(", ", uniqueRoles);

            String existingDescription = operation.getDescription() != null ? operation.getDescription() : "";
            operation.setDescription(existingDescription + "\n\n**Erlaubte Rollen:** " + cleanedRoles);
        }
    }

    private static void extractRolesFromExpression(String spelExpression, List<String> extractedRoles) {
        if (spelExpression != null) {
            Matcher matcher = ROLE_PATTERN.matcher(spelExpression);
            while (matcher.find()) {
                extractedRoles.add(matcher.group(1));
            }
        }
    }

    //Wird nur gesetzt, wenn die @RequiresOrganisationContext Annotation gesetzt ist
    private static void addOrganisationContextHeader(Operation operation, HandlerMethod handlerMethod,
            RequestContextProperties contextProperties) {

        boolean hasAnnotation = handlerMethod.hasMethodAnnotation(RequiresOrganisationContext.class)
                || handlerMethod.getBeanType().isAnnotationPresent(RequiresOrganisationContext.class);

        if (hasAnnotation){
            Parameter optionalHeader = new Parameter()
                    .in("header")
                    .name(contextProperties.getOrgHeader())
                    .description("Context Organisation UUID")
                    .required(true)
                    .schema(new Schema<String>()
                            .type("string")
                            .format("uuid")
                            .example("ce34c847-f570-1604-8387-b5d753a9251d")
                    );

            operation.addParametersItem(optionalHeader);
        }
    }
}
