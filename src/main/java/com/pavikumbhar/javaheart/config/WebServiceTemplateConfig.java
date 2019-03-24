package com.pavikumbhar.javaheart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.WebServiceTemplate;


/**
 *  This  Configurations required for WebService Response
 * @author pavikumbhar
 *
 */
@Configuration
public class WebServiceTemplateConfig {

  
    @Bean
    public WebServiceTemplate webServicetemplate() {
        return new WebServiceTemplate();
    }
}
