package com.pavikumbhar.javaheart.ws;

import javax.jws.WebService;

import lombok.extern.slf4j.Slf4j;

@WebService(serviceName = "HelloService", portName = "HelloPort", targetNamespace = "http://com.pavikumbhar.javaheart/", endpointInterface = "com.pavikumbhar.javaheart.ws.Hello")
@Slf4j
public class HelloPortImpl implements Hello {
    
    @Override
    public String sayHello(String myname) {
        log.info("Executing operation sayHello" + myname);
        try {
            return "Hello, Welcome to CXF Spring boot " + myname + "!!!";
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
}