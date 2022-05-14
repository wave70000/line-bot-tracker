package com.line.bot;

import com.line.bot.config.LineUniqueId;
import com.line.bot.firebase.MemberService;
import com.linecorp.bot.model.message.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WakeupController {
    private final Logger logger = LoggerFactory.getLogger(WakeupController.class);


    @GetMapping("/wakeup")
    public String wakeUp() {
        System.out.println("I'm Wakeup Now!!!");
        logger.info("App has been waking up");
        return "I'm Wakeup now!!";

    }
}
