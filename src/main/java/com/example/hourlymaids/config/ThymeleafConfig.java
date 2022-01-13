/**
 *
 */
package com.example.hourlymaids.config;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * @author at-tuandang
 *
 */
@Configuration
public class ThymeleafConfig {

	/** Get template folder. */
    @Value("${spring.mail.template}")
    private String tempFolder;
    @Value("${spring.mail.template.suffix}")
    private String tempSuffix;

    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver htmlTemplateResolver(){
        SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
        emailTemplateResolver.setPrefix(tempFolder);
        emailTemplateResolver.setSuffix(tempSuffix);
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML5.name());
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return emailTemplateResolver;
    }
}
