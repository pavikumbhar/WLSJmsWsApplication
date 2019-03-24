package com.pavikumbhar.javaheart.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Configuration class to define the required attributes for properties loading.<br>
 * Filename and whether it needs to be reloaded on any change on-the-fly 
 * need to be defined here.
 */
@AllArgsConstructor
@Getter
public class PropertyConfigParameter {
    
    private String fileName;
    private boolean isReload;
    
}
