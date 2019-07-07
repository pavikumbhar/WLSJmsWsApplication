package com.pavikumbhar.javaheart.config.jms;

import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_ONE_CONCURRENCY;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_ONE_CONCURRENT_CONSUMERS;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_ONE_CONN_FACTORY;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_ONE_REQUEST_QUEUE;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_ONE_RESPONSE_QUEUE;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_ONE_URL;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.jms.Destination;
import javax.jms.QueueConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiTemplate;

import com.pavikumbhar.javaheart.config.JmsDestinationParameter;
import com.pavikumbhar.javaheart.config.WLSJmsConfiguration;
import com.pavikumbhar.javaheart.jms.listener.ServiceOneMessageListener;

/**
 * Configurer for ServiceOne JMS interface
 */
@Configuration
@ConditionalOnProperty(value = "serviceone", havingValue = "true")
public class ServiceOneJmsConfigurer extends WLSJmsConfiguration {
    
    @Autowired
    private ServiceOneMessageListener messageListener;
    
    @Primary
    @PostConstruct
    public QueueConnectionFactory serviceOneConnectionFactory() {
        return getConnectionFactory(SERVICE_ONE_CONN_FACTORY);
    }
    
    @Bean
    public Properties serviceOneJndiProperties() {
        return createJndiProperties(SERVICE_ONE_URL);
    }
    
    @Bean
    public JndiTemplate serviceOneJndiTemplate() {
        return createJndiTemplate(serviceOneJndiProperties());
    }
    
    @Bean
    public JndiDestinationResolver serviceOneJndiDestinationResolver() {
        return createJndiDestinationResolver(serviceOneJndiTemplate());
    }
    
    /** Template for responses */
    @Bean
    public JmsTemplate serviceOneJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(serviceOneConnectionFactory());
        jmsTemplate.setDefaultDestination(serviceOneResponseQueue());
        return jmsTemplate;
    }
    
    /** Queue on which response is to be posted */
    @Bean
    public Destination serviceOneResponseQueue() {
        return lookupResource(getProperty(SERVICE_ONE_RESPONSE_QUEUE), Destination.class);
    }
    
    @Override
    protected Properties jndiProperties() {
        return serviceOneJndiProperties();
    }
    
    @Bean
    public DefaultMessageListenerContainer serviceOneJmsContainer() {
        JmsDestinationParameter jmsDestinationParameter = new JmsDestinationParameter(SERVICE_ONE_REQUEST_QUEUE, serviceOneConnectionFactory(),
                serviceOneJndiDestinationResolver(), SERVICE_ONE_CONCURRENCY, SERVICE_ONE_CONCURRENT_CONSUMERS, messageListener, serviceOneJndiProperties());
        return createJmsContainer(jmsDestinationParameter);
    }
    
}
