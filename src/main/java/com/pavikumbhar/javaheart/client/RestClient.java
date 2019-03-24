package com.pavikumbhar.javaheart.client;

import java.net.URI;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author pavikumbhar
 *
 */

@Slf4j
public class RestClient {
	

	/**
	 * 
	 * @param uri
	 * @param request
	 * @param bodyType
	 * @return
	 */
    public static <T,E>  E synchronousCall(String uri, T request, Class<E> bodyType ) {
        log.info("Making synchronous rest call...  uri : {} request :{} " ,uri,request.toString());
        E response = WebClient.create().
        						post()
        						.uri(URI.create(uri))
        						.body(BodyInserters.fromObject(request))
        						.retrieve().
        						 bodyToMono(bodyType) //The method bodyToMono() extracts the body to a Mono instance
        						.block(); //The method Mono.block() subscribes to this Mono instance and blocks until the response is received. 
        log.info("synchronousCall() Sync Rest call completed...");
        log.debug(response.toString());
        return response;
    }

    /**
     * 
     * @param uri
     * @param request
     * @param bodyType
     */
    public static   <T,E> void asynchronousCall(String uri, T request, Class<E> bodyType ) {
    	log.info("RestClient.asyncCall() -Making asynchronousCall rest call...uri  : {} request :{} " ,uri, request.toString());
    	WebClient.create()
    	            .post()
    	            .uri(URI.create(uri))
    	            .body(BodyInserters.fromObject(request))
    	            .retrieve()
                    .bodyToMono(bodyType)
                    .subscribe();   //Mono.subscribe() returns immediately (as opposed to block() method which waits for the full response)
    	           // .subscribe(RestClient::handleResponse);
    	log.info("RestClient.asynchronousCall() Async Rest call completed...");
       

    }

    
    private static <E>void handleResponse(E response) {
        log.debug(response.toString());
    }


    public static void main(String[] args) {
        String requestString = "Pravin Kumbhar ";
        String uri = "http://localhost:8080/prespons";
        RestClient.asynchronousCall(uri,requestString,String.class);
    }


}
