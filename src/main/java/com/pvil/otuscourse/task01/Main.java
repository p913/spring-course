package com.pvil.otuscourse.task01;

import com.pvil.otuscourse.task01.service.TestRunnerService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

@PropertySource("classpath:appconfig.properties")
@ComponentScan
@Configuration //если в данном классе не создавать бины, можно без @Configuration
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class);

        TestRunnerService testRunner = context.getBean(TestRunnerService.class);

        if (testRunner.runTests())
            testRunner.showResults();
    }

    /* в spring 5 уже не нужно?
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }*/

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("/i18n/bundle");
        ms.setDefaultEncoding("UTF-8");
        return ms;
    }
}
