package com.pavikumbhar.javaheart.config;

import java.util.Properties;

import javax.jms.MessageListener;
import javax.jms.QueueConnectionFactory;

import org.springframework.jms.support.destination.JndiDestinationResolver;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author pavikumbhar
 *
 */
@AllArgsConstructor
@Getter
public class JmsDestinationParameter {
    
    private String queueName;
    
    private QueueConnectionFactory connectionFactory;
    
    private JndiDestinationResolver jndiDestinationResolver;
    
    private String concurrency;
    /**
     * Specify the number of concurrent consumers to create. Default is 1.
     */
    private String concurrentConsumers;
    
    private MessageListener messageListener;
    
    private Properties jndiProperties;
    
}
