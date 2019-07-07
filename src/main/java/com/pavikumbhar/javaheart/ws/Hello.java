package com.pavikumbhar.javaheart.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://pavikumbhar.javaheart/", name = "Hello")
public interface Hello {
    
    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "sayHello", targetNamespace = "http://pavikumbhar.javaheart/", className = "com.pavikumbhar.javaheart.ws.SayHello")
    @WebMethod(action = "urn:SayHello")
    @ResponseWrapper(localName = "sayHelloResponse", targetNamespace = "http://pavikumbhar.javaheart", className = "com.pavikumbhar.javaheart.ws.SayHelloResponse")
    String sayHello(@WebParam(name = "myname", targetNamespace = "") String myname);
}