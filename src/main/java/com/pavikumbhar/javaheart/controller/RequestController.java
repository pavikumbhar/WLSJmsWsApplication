package com.pavikumbhar.javaheart.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pavikumbhar.javaheart.config.PropertyManager;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RequestController {
    
    @Autowired
    private JmsTemplate serviceOneJmsTemplate;
    
    @Autowired
    private JmsTemplate serviceTwoJmsTemplate;
    
    @Autowired
    private PropertyManager propertyManager;
    
    @PostMapping(value = "/serviceOneJmsResponse")
    public void serviceOneJmsResponse(@RequestBody String message) {
        log.debug("serviceOneJmsResponse: {}", message);
        serviceOneJmsTemplate.convertAndSend(message);
        
    }
    
    @PostMapping(value = "/serviceTwoJmsResponse")
    public void serviceTwoJmsResponse(@RequestBody String message) {
        log.debug("serviceTwoResponseMessage: {}", message);
        serviceTwoJmsTemplate.convertAndSend(message);
        
    }
    
    @Scheduled(cron = "*/5 * * * * ?")
    public void demoServiceMethod() {
        log.info("Method executed at every 5 seconds. Current time is :: {} ", new Date());
        log.info("reloadableProperties :: {} ", propertyManager.getProperty("reloadableProperties"));
        
    }
}
