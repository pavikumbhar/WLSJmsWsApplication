package com.pavikumbhar.javaheart.config.ws;


import javax.xml.ws.Endpoint;

import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pavikumbhar.javaheart.config.WSEndpointConfiguration;
import com.pavikumbhar.javaheart.ws.HelloPortImpl;

@Configuration
public class HelloWSConfig  extends WSEndpointConfiguration{
	
	
	 @Bean
	    public Endpoint endpoint() {
	        EndpointImpl endpoint = new EndpointImpl(bus, new HelloPortImpl());
	        endpoint.publish("/Hello");
	        return endpoint;
	}

}
