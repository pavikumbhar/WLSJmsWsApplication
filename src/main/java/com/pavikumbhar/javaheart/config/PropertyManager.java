package com.pavikumbhar.javaheart.config;


import static com.pavikumbhar.javaheart.constants.AppConstants.JMS_PROPERTIES;
import static com.pavikumbhar.javaheart.constants.AppConstants.WS_PROPERTIES;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Manager class for all the property files in application.<br>
 * Internally manages changes to property files on-the-fly and
 * provides the latest values.
 */
@Component
public class PropertyManager {
    
    @Value("${app.propertyLoc}")
    private String baseLocation;
    
    @Autowired
    private CompositeConfiguration configuration;
    
    /**
     * 
     * @param key
     * @return Value of the property corresponding to the key
     */
    public String getProperty(String key) {
        return (String) configuration.getProperty(key);
    }
    
    @Bean
    public CompositeConfiguration getPropertiesConfiguration() {
        List<PropertyConfigParameter> propertyConfigParameters = new ArrayList<>();
        propertyConfigParameters.add(new PropertyConfigParameter(JMS_PROPERTIES, false));
        propertyConfigParameters.add(new PropertyConfigParameter(WS_PROPERTIES, true));
        return PropertyConfiguration.createPropertiesConfiguration(baseLocation, propertyConfigParameters);
    }
    
}
