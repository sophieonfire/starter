package de.nrw.it.ignrw.autoconfig;

import de.nrw.it.ignrw.handler.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

@AutoConfiguration
@ConditionalOnWebApplication(type = SERVLET)
@ConditionalOnClass({
        MethodArgumentNotValidException.class,
        ConstraintViolationException.class
})
public class ExceptionHandlerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    GlobalExceptionHandler globalExceptionHandler(MessageSource messageSource) {
        return new GlobalExceptionHandler(messageSource);
    }
}
