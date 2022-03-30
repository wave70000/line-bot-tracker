package com.line.bot.config;

import com.line.bot.LineBotController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class CronScheduler {

    public static String netflixCronTime = "30 * * * * *";
//    private final Logger logger = LoggerFactory.getLogger(CronScheduler.class);

//    @Scheduled(cron = "${cron.expression}")
//    public void cronScheduleTask() {
//        logger.info("Scheduled tasks - {}", ZonedDateTime.now());
//    }
}
