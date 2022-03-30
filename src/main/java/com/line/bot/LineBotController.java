package com.line.bot;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@LineMessageHandler
public class LineBotController {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @EventMapping
    public void handleMessageEvent(MessageEvent<TextMessageContent> event) {
        System.out.println("event: " + event);
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event,message);
//        return new TextMessage(event.getMessage().getText());
    }

    @EventMapping
    public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) {
        String userId = event.getSource().getUserId();
        thankMessage(userId);
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) {
        String text = content.getText();
//        reply(replyToken,content);
//        System.out.println(text);

        switch (text) {
            case "Image": {
                String userId = event.getSource().getUserId();
                thankMessage(userId);
//                String str = "https://i0.wp.com/applewoodfresh.com/wp-content/uploads/2018/11/apples_jonagold.png?fit=600%2C600&ssl=1";
//                pushImage(userId,str);
//                reply(replyToken,imageMessage);
            }
        }
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
    private void replyText(String replyToken,TextMessageContent content) {
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
