package com.pavikumbhar.javaheart.config.jms;

import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_TWO_CONCURRENCY;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_TWO_CONCURRENT_CONSUMERS;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_TWO_CONN_FACTORY;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_TWO_REQUEST_QUEUE;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_TWO_RESPONSE_QUEUE;
import static com.pavikumbhar.javaheart.constants.JmsConstants.SERVICE_TWO_URL;

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
import com.pavikumbhar.javaheart.jms.listener.ServiceTwoMessageListener;

/**
 * 
 * @author pavikumbhar Configurer for ServiceTwo JMS interface
 */
@Configuration
@ConditionalOnProperty(value = "servicetwo", havingValue = "true")
public class ServiceTwoJmsConfigurer extends WLSJmsConfiguration {
    
    @Autowired
    private ServiceTwoMessageListener messageListener;
    
    @Primary
    @PostConstruct
    public QueueConnectionFactory serviceTwoConnectionFactory() {
        return getConnectionFactory(SERVICE_TWO_CONN_FACTORY);
    }
    
    @Bean
    public Properties serviceTwoJndiProperties() {
        return createJndiProperties(SERVICE_TWO_URL);
    }
    
    @Bean
    public JndiTemplate serviceTwoJndiTemplate() {
        return createJndiTemplate(serviceTwoJndiProperties());
    }
    
    @Bean
    public JndiDestinationResolver serviceTwoJndiDestinationResolver() {
        return createJndiDestinationResolver(serviceTwoJndiTemplate());
    }
    
    /** Template for responses */
    @Bean
    public JmsTemplate serviceTwoJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(serviceTwoConnectionFactory());
        jmsTemplate.setDefaultDestination(serviceTwoResponseQueue());
        return jmsTemplate;
    }
    
    /** Queue on which response is to be posted */
    @Bean
    public Destination serviceTwoResponseQueue() {
        return lookupResource(getProperty(SERVICE_TWO_RESPONSE_QUEUE), Destination.class);
    }
    
    @Override
    protected Properties jndiProperties() {
        return serviceTwoJndiProperties();
    }
    
    @Bean
    public DefaultMessageListenerContainer serviceTwoJmsContainer() {
        JmsDestinationParameter jmsDestinationParameter = new JmsDestinationParameter(SERVICE_TWO_REQUEST_QUEUE, serviceTwoConnectionFactory(),
                serviceTwoJndiDestinationResolver(), SERVICE_TWO_CONCURRENCY, SERVICE_TWO_CONCURRENT_CONSUMERS, messageListener, serviceTwoJndiProperties());
        return createJmsContainer(jmsDestinationParameter);
    }
    
}
