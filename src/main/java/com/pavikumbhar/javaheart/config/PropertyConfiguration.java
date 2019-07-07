package com.pavikumbhar.javaheart.config;

import java.util.List;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to create property related configurations.
 * 
 * @author pavikumbhar
 *
 */
@Slf4j
public class PropertyConfiguration {
    
    private static final char DELIMITER = '\n';
    
    static {
        // Default delimiter is a comma (,). Change it to newline char
        AbstractConfiguration.setDefaultListDelimiter(DELIMITER);
    }
    
    private PropertyConfiguration() {
    }
    
    private static Configuration createConfig(String baseLocation, PropertyConfigParameter propertyConfigParameter) {
        PropertiesConfiguration propertiesConfiguration = null;
        try {
            propertiesConfiguration = new PropertiesConfiguration(baseLocation + propertyConfigParameter.getFileName());
            if (propertyConfigParameter.isReload()) {
                // It's important to set new object of reload strategy for each config
                propertiesConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());
            }
        } catch (ConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        return propertiesConfiguration;
    }
    
    /**
     * Creates a <code>CompositeConfiguration</code> with all the properties provided
     * 
     * @param baseLocation
     *            - Location where property files are located
     * @param propertyConfigs
     *            - List of configurations to be used
     * @return - A <code>CompositeConfiguration</code> with all the properties provided
     */
    @SneakyThrows
    public static CompositeConfiguration createPropertiesConfiguration(String baseLocation, List<PropertyConfigParameter> propertyConfigParameters) {
        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
        propertyConfigParameters.forEach(propertyConfig -> compositeConfiguration.addConfiguration(createConfig(baseLocation, propertyConfig)));
        return compositeConfiguration;
    }
    
}
