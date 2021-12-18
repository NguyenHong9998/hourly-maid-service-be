package com.example.hourlymaids.config;
 
import java.util.Locale; 
import java.util.List; 
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.context.support.ResourceBundleMessageSource;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class CustomLocaleResolver 
             extends AcceptHeaderLocaleResolver 
             implements WebMvcConfigurer {

   List<Locale> LOCALES = Arrays.asList(
         new Locale("en"),
         new Locale("ja"));

   /**
    * Resolvel local language
    * 
    * @author st-ngatran
    * @param request
    * @return
    */
    @Override
   public Locale resolveLocale(HttpServletRequest request) {
      String headerLang = request.getHeader("Accept-Language");
      return headerLang == null || headerLang.isEmpty()
            ? Locale.getDefault()
            : Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES);
   }

   /**
    * Config for message define
    * 
    * @author st-ngatran
    * @return
    */
   @Bean
   public ResourceBundleMessageSource messageSource() {
      ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
      messageSource.setBasename("messages");
      messageSource.setDefaultEncoding("UTF-8");
      messageSource.setUseCodeAsDefaultMessage(true);
      return messageSource;
   }
}
