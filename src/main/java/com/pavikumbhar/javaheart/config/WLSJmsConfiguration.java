package com.pavikumbhar.javaheart.config;

import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;
import static javax.naming.Context.PROVIDER_URL;
import static javax.naming.Context.SECURITY_CREDENTIALS;
import static javax.naming.Context.SECURITY_PRINCIPAL;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Properties;

import javax.jms.Destination;
import javax.jms.QueueConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiTemplate;

import lombok.extern.slf4j.Slf4j;
import weblogic.jndi.WLInitialContextFactory;

/**
 * @author pavi kumbhar
 * 
 *         <p>
 *         Configuration file for weblogic JMS connection
 */
@Slf4j
public abstract class WLSJmsConfiguration {
    
    @Autowired
    private PropertyManager environment;
    
    /** @return Initial Context properties */
    protected abstract Properties jndiProperties();
    
    /**
     * username of weblogic server using which JNDI connection will be established
     * 
     * @note here i am using : for default null This method fetches the User Name
     * @return
     */
    protected String getUserName() {
        return EMPTY;
    }
    
    /**
     * password of weblogic server using which JNDI connection will be established
     * 
     * @note here i am using : for default null
     * 
     *       This method fetches the Password
     * @return
     */
    protected String getPassword() {
        return EMPTY;
    }
    
    /**
     * 
     * @param jndiName
     * @param requiredType
     * @return
     */
    @SuppressWarnings("unchecked")
    protected final <T> T lookupResource(String jndiName, Class<T> requiredType) {
        
        try {
            InitialContext initialContext = new InitialContext(jndiProperties());
            Object located = initialContext.lookup(jndiName);
            if (located == null) {
                log.error("JNDI object with [{}] not found", jndiName);
                throw new NameNotFoundException("JNDI object with [" + jndiName + "] not found");
            }
            return (T) located;
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * @param key
     * @return
     */
    protected final String getProperty(String key) {
        return environment.getProperty(key);
    }
    
    /**
     * 
     * 
     * Get JNDI properties from properties file
     * 
     * return list of Weblogic jndi properties
     * 
     * @param url
     * 
     *            Constant that holds the name of the environment property for specifying
     *            configuration information for the service provider to use. The value of the
     *            property should contain a URL string (e.g. "t3://localhost:8001").
     * @return
     */
    protected final Properties createJndiProperties(String url) {
        log.info("createJndiProperties ConnectionFactory url : {}", url);
        Properties jndiProperties = new Properties();
        jndiProperties.put(INITIAL_CONTEXT_FACTORY, WLInitialContextFactory.class.getName());
        jndiProperties.put(PROVIDER_URL, getProperty(url));
        if (isNotEmpty(getUserName()) && isNotEmpty(getPassword())) {
            jndiProperties.setProperty(SECURITY_PRINCIPAL, getUserName());
            jndiProperties.setProperty(SECURITY_CREDENTIALS, getPassword());
        }
        
        return jndiProperties;
    }
    
    /**
     * 
     * @param connectionFactoryName
     * @return
     */
    protected final QueueConnectionFactory getConnectionFactory(String connectionFactoryName) {
        return lookupResource(getProperty(connectionFactoryName), QueueConnectionFactory.class);
    }
    
    /**
     * Create JNDI template.
     * 
     * @param jndiProperties
     * @return
     */
    protected final JndiTemplate createJndiTemplate(Properties jndiProperties) {
        JndiTemplate jndiTemplate = new JndiTemplate();
        jndiTemplate.setEnvironment(jndiProperties);
        return jndiTemplate;
    }
    
    /**
     * Create DestinationResolver
     * 
     * @param jndiTemplate
     * @return
     */
    protected final JndiDestinationResolver createJndiDestinationResolver(JndiTemplate jndiTemplate) {
        JndiDestinationResolver jndiDestinationResolver = new JndiDestinationResolver();
        jndiDestinationResolver.setJndiTemplate(jndiTemplate);
        jndiDestinationResolver.setCache(true);
        return jndiDestinationResolver;
    }
    
    /**
     * 
     * @param jmsDestParameter
     * @return
     */
    protected final DefaultMessageListenerContainer createJmsContainer(JmsDestinationParameter jmsDestParameter) {
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(jmsDestParameter.getConnectionFactory());
        defaultMessageListenerContainer.setDestinationResolver(jmsDestParameter.getJndiDestinationResolver());
        defaultMessageListenerContainer.setDestination(lookupResource(getProperty(jmsDestParameter.getQueueName()), Destination.class));
        defaultMessageListenerContainer.setConcurrency(getProperty(jmsDestParameter.getConcurrency()));
        defaultMessageListenerContainer.setConcurrentConsumers(Integer.parseInt(getProperty(jmsDestParameter.getConcurrentConsumers())));
        defaultMessageListenerContainer.setMessageListener(jmsDestParameter.getMessageListener());
        // defaultMessageListenerContainer.setSessionAcknowledgeModeName(javax.jms.Session.AUTO_ACKNOWLEDGE);
        defaultMessageListenerContainer.setSessionAcknowledgeMode(javax.jms.Session.AUTO_ACKNOWLEDGE);
        // defaultMessageListenerContainer.setCacheLevelName(5);
        defaultMessageListenerContainer.setCacheLevel(5);
        return defaultMessageListenerContainer;
    }
    
}
