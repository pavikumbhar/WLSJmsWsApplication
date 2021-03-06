package com.pavikumbhar.javaheart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

/**
 * 
 * @author pavikumbhar
 *
 */
@SpringBootApplication
@PropertySource("/properties/service.properties")
@PropertySource("/properties/scheduler.properties")
@EnableAutoConfiguration(exclude = { QuartzAutoConfiguration.class })
public class WLSJmsWsApplication extends SpringBootServletInitializer {
    
    public static void main(String[] args) {
        SpringApplication.run(WLSJmsWsApplication.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WLSJmsWsApplication.class);
    }
    
}
