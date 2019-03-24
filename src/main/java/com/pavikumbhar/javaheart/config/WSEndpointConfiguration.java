package com.pavikumbhar.javaheart.config;

import org.apache.cxf.Bus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author pavikumbhar
 * 
 *         Abstract class for all the Web service based end point configurations
 *
 */
public abstract class WSEndpointConfiguration {

	@Autowired
	protected Bus bus;

}
