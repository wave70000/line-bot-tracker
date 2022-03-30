package com.line.LineBot;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@LineMessageHandler
public class LineBotApplication {
	private static Logger logger = LoggerFactory.getLogger(LineBotApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(LineBotApplication.class, args);
	}

	@EventMapping
	public Message handleMessageEvent(MessageEvent<TextMessageContent> event) {
		logger.info("event: " +
				event.getMessage().getId() + " : " +
				event.getMessage().getText());
//		System.out.println("event: " + event);
		return new TextMessage(event.getMessage().getText());
	}

	@EventMapping
	public void handleDefaultMessageEvent(Event event) {
//		System.out.println("event: " + event);
	}

}
