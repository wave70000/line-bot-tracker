package com.line.bot;

import com.line.bot.firebase.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LineBotApplication {

	private static final Logger logger = LoggerFactory.getLogger(LineBotApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LineBotApplication.class, args);

	}

}
