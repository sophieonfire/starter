package de.nrw.it.ignrw.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@AutoConfiguration
@ConditionalOnClass(MessageSource.class)
public class MessageSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MessageSource.class)
    public MessageSource messageSource() {

        ReloadableResourceBundleMessageSource ms =
                new ReloadableResourceBundleMessageSource();

        ms.setBasenames(
                // Consumer-Messages (überschreiben Starter)
                "classpath:i18n/errors",
                "classpath:i18n/messages",
                "classpath:i18n/validation",

                // Gemeinsame ignrw-Messages
                "classpath:i18n/ignrw-errors",
                "classpath:i18n/ignrw-messages",
                "classpath:i18n/ignrw-validation"
        );

        ms.setDefaultEncoding("UTF-8");
        ms.setFallbackToSystemLocale(false);
        return ms;
    }
}
