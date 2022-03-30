package com.line.bot;

import com.line.bot.config.LineGroupId;
import com.line.bot.config.QRCodeUrl;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.LeaveEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@LineMessageHandler
@Component
public class LineBotController {
    private final Logger logger = LoggerFactory.getLogger(LineBotController.class);

    @Autowired
    private LineMessagingClient lineMessagingClient;

    final String netflixRoom = LineGroupId.netflixGroupId;
    //    @Scheduled(cron = "${cron.expression}")
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Bangkok")
    public void cronScheduleTaskYoutube() {
        logger.info("Scheduled tasks - {}", ZonedDateTime.now());
        pushText(netflixRoom,new TextMessage("Time to pay YouTube Premium!!!"));
        pushImage(netflixRoom,QRCodeUrl.youtubePrompPayUrl);
    }

    private final String youtubeRoom = LineGroupId.youtubeGroupId;

    @EventMapping
    public void handleMessageEvent(MessageEvent<TextMessageContent> event) {
        System.out.println("event: " + event);
        if (roomSplitter(event)) {
            TextMessageContent message = event.getMessage();
            handleTextContent(event.getReplyToken(), event,message);
        }
    }

    @EventMapping
    public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) {
        System.out.println("event: " + event);
        if (roomSplitter(event)) {
            String userId = event.getSource().getUserId();
            thankMessage(userId);
        }
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        String replyToken = event.getReplyToken();
        replyText(replyToken, new TextMessage("Joined " + event.getSource()));
    }

    @EventMapping
    public void handleLeaveEvent(LeaveEvent event) {
        System.out.println("Leaved " + event.getSource());
    }

    @EventMapping
    public void handleDefaultEvent(Event event) {
        System.out.println(event);
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) {
        String text = content.getText();
        String userId = event.getSource().getUserId();
//        reply(replyToken,content);
//        System.out.println(text);

        switch (text) {
            case "Image": {
                thankMessage(userId);
//                String str = "https://i0.wp.com/applewoodfresh.com/wp-content/uploads/2018/11/apples_jonagold.png?fit=600%2C600&ssl=1";
//                pushImage(userId,str);
//                reply(replyToken,imageMessage);
                break;
            }
            case "Profile": {
                getProfile(userId);
                break;
            }
            case "All Room": {
                ArrayList<String> roomList = new ArrayList<>();
                roomList.add(netflixRoom);
                roomList.add(youtubeRoom);
                replyText(replyToken,new TextMessage(roomList.toString()));
                break;
            }
            case "test qr": {
                String qr = QRCodeUrl.youtubePrompPayUrl;
                pushImage(event.getSource().getSenderId(),qr);
                break;
            }
        }
    }

    // Check message come from expect group or not
    private boolean roomSplitter(Event event) {
        String groupId = event.getSource().getSenderId();
        return groupId.equals(netflixRoom) || groupId.equals(youtubeRoom);
    }

    // Push Image Message to UserId,GroupId or RoomId with URL
    private void pushImage(String pushId,String imageUrl) {
        URI uri;
        try {
            uri = new URI(imageUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        ImageMessage imageMessage = new ImageMessage(uri,uri);
        PushMessage pushMessage = new PushMessage(pushId,imageMessage);
        BotApiResponse botApiResponse;
        try {
            botApiResponse = lineMessagingClient.pushMessage(pushMessage).get();
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            return;
        }
        System.out.println("Successfully Pushed Image");
    }

    // Reply Text message to sender with ReplyToken
    private void replyText(String replyToken,TextMessage content) {
        String message = content.getText();
        TextMessage textMessage = new TextMessage(message);
        ReplyMessage replyMessage = new ReplyMessage(
                replyToken,
                textMessage);
        BotApiResponse botApiResponse;
        try {
            botApiResponse = lineMessagingClient.replyMessage(replyMessage).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }
        System.out.println(botApiResponse);
    }

    // Push Text Message to UserId,GroupId or RoomId
    private void pushText(String pushId,TextMessage content) {
        String message = content.getText();
        TextMessage textMessage = new TextMessage(message);
        PushMessage pushMessage = new PushMessage(pushId,content);
        BotApiResponse botApiResponse;
        try {
            botApiResponse = lineMessagingClient.pushMessage(pushMessage).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }
        System.out.println(botApiResponse);
    }

    // Say "Profile" to get userID
    private void getProfile(String userId) {
        String displayName;
        UserProfileResponse userProfileResponse;
        try {
            userProfileResponse = lineMessagingClient.getProfile(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }
        displayName = userProfileResponse.getDisplayName();
        System.out.println("User ID: " + userProfileResponse.getUserId() + " Display name: " + userProfileResponse.getDisplayName());
        pushText(userId,new TextMessage(userId));
    }

    // Push Thanks <DisplayName>
    private void thankMessage(String userId) {
        if (userId != null) {
            UserProfileResponse userProfileResponse;
            try {
                userProfileResponse = lineMessagingClient.getProfile(userId).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return;
            }
            pushText(userId,new TextMessage("Thanks " +userProfileResponse.getDisplayName()));
        }
    }

}
