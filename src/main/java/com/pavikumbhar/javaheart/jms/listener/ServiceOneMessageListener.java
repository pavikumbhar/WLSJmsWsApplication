package com.pavikumbhar.javaheart.jms.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ServiceOneMessageListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		String txtMessage=null;
		try {
			if (message instanceof TextMessage) {
				 txtMessage = ((TextMessage) message).getText();
			}
			log.debug("Message on: {} is: {}", message.getJMSDestination(), txtMessage);
		} catch (JMSException e) {
			log.error(e.getMessage(), e);
		}
	}

}
