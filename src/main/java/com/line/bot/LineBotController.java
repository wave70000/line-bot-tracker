package com.line.bot;

import com.google.firebase.database.DatabaseReference;
import com.line.bot.config.LineGroupId;
import com.line.bot.config.LineUniqueId;
import com.line.bot.config.QRCodeUrl;
import com.line.bot.firebase.FirebaseInitialize;
import com.line.bot.firebase.MemberService;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@LineMessageHandler
@Component
public class LineBotController {
    private final Logger logger = LoggerFactory.getLogger(LineBotController.class);

    @Autowired
    private LineMessagingClient lineMessagingClient;

    // Firebase DI
    private final MemberService memberService;
    public LineBotController(MemberService memberService) {
        this.memberService = memberService;
    }

    private final String youtubeRoom = LineGroupId.youtubeGroupId;
    // cron *(second) *(minute) *(hour) *(day) *(month) *(dayOfWeek)
    @Scheduled(cron = "0 0 9 3 * *", zone = "Asia/Bangkok")
    public void cronScheduleTaskYoutube() {
        logger.info("Scheduled tasks - {}", ZonedDateTime.now());
        pushText(youtubeRoom,new TextMessage("Time to pay YouTube Premium!!!"));
        pushSticker(youtubeRoom,"6632","11825377");
        pushImage(youtubeRoom,QRCodeUrl.youtubePrompPayUrl);
        memberService.updateDueDate("youtube",LocalDateTime.now(ZoneId.of("Asia/Bangkok")));
    }

    private final String netflixRoom = LineGroupId.netflixGroupId;
    @Scheduled(cron = "0 0 9 26 * *", zone = "Asia/Bangkok")
    public void cronScheduleTaskNetflix() {
        logger.info("Scheduled tasks - {}", ZonedDateTime.now());
        pushText(netflixRoom, new TextMessage("Time to pay Netflix!!!"));
        pushSticker(netflixRoom, "6632", "11825377");
        pushImage(netflixRoom, QRCodeUrl.netflixPrompPayUrl);
        memberService.updateDueDate("netflix",LocalDateTime.now(ZoneId.of("Asia/Bangkok")));
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Bangkok")
    public void cronScheduleTaskCheckAllPay() {
        logger.info("Scheduled tasks - {}", ZonedDateTime.now());

        //TODO Enable when ready
//        if (!memberService.checkLastPay("netflix")) {
//            pushText(netflixRoom, new TextMessage("จ่ายเงินกันด้วยน้าา"));
//            pushSticker(netflixRoom, "6632", "11825374");
//        }
//
//        if (!memberService.checkLastPay("youtube")) {
//            pushText(youtubeRoom, new TextMessage("จ่ายเงินกันด้วยน้าา"));
//            pushSticker(youtubeRoom, "6632", "11825374");
//        }

//        if (!memberService.checkLastPay("youtube")) {
//            pushText(LineUniqueId.ADMIN_ID, new TextMessage("จ่ายเงินกันด้วยน้าา"));
//            pushSticker(LineUniqueId.ADMIN_ID, "6632", "11825374");
//        }

    }

    @EventMapping
    public void handleMessageEvent(MessageEvent<TextMessageContent> event) {
//        logger.info("Event: " + event.getMessage().getText());
        if (roomSplitter(event)) {
            TextMessageContent message = event.getMessage();
            handleTextContent(event.getReplyToken(), event,message);
        }
        //TODO NEED TO DELETED WHEN DEPLOY
//        else if (event.getSource().getUserId().equals(LineUniqueId.ADMIN_ID)) {
//            logger.info("Event from Admin");
//            pushText(event.getSource().getSenderId(),new TextMessage("@"+getProfile(LineUniqueId.ADMIN_ID)));
//        }

    }

    @EventMapping
    public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) {
        System.out.println("event: " + event);
        if (roomSplitter(event)) {
            String userId = event.getSource().getUserId();

            String groupId = event.getSource().getSenderId();
            if (groupId.equals(netflixRoom)){
                memberService.updateLastPay("netflix",userId);
            }
            if (groupId.equals(youtubeRoom)) {
                memberService.updateLastPay("youtube",userId);
            }
            pushSticker(event.getSource().getSenderId(), "6136", "10551380");
        }
        //TODO Test server response when server has sleep (NEED TO DELETED WHEN DEPLOY)
//        else {
//            logger.info("Event from Admin");
//            String userId = event.getSource().getUserId();
//            memberService.updateLastPay("youtube",userId);
//            pushText(event.getSource().getSenderId(),new TextMessage("@"+getProfile(LineUniqueId.ADMIN_ID)));
//        }
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        String adminId = "U61f804b24760bf01490c70f3791b8905";
        Timestamp ts = new Timestamp(event.getTimestamp().toEpochMilli());
        pushText(adminId, new TextMessage("Joined at " + ts));
        pushText(adminId,new TextMessage(event.getSource().getSenderId()));
    }

    @EventMapping
    public void handleLeaveEvent(LeaveEvent event) {
        System.out.println("Leaved " + event.getSource());
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) {
        String adminId = "U61f804b24760bf01490c70f3791b8905";
        System.out.println(event.getSource().getUserId()+" add me to fried");
        pushText(adminId, new TextMessage(event.getSource().getUserId()+" add me to fried"));

    }

    @EventMapping
    public void handleMemberJoined(MemberJoinedEvent event) {
        Timestamp ts = new Timestamp(event.getTimestamp().toEpochMilli());
        pushText(LineUniqueId.ADMIN_ID, new TextMessage(event.getJoined().getMembers() + " Joined at " + ts));
        String userId = event.getJoined().getMembers().get(0).getUserId();

        if (event.getSource().getSenderId().equals(netflixRoom)){
            memberService.addNewMember("netflix",userId);
        }
        if (event.getSource().getSenderId().equals(youtubeRoom)){
            memberService.addNewMember("youtube",userId);
        }
//        System.out.println(userId);
//        System.out.println(getProfile(userId));
//        getProfile(event.getJoined().getMembers().get(0).getUserId());
    }

    @EventMapping
    public void handleMemberLeaved(MemberLeftEvent event) {
        Timestamp ts = new Timestamp(event.getTimestamp().toEpochMilli());
        pushText(LineUniqueId.ADMIN_ID, new TextMessage(event.getLeft().getMembers() + " Leaved at " + ts));
        String userId = event.getLeft().getMembers().get(0).getUserId();

        if (event.getSource().getSenderId().equals(netflixRoom)) {
            memberService.deleteMember("netflix",userId);
        }
        if (event.getSource().getSenderId().equals(youtubeRoom)) {
            memberService.deleteMember("youtube",userId);
        }

//        System.out.println(userId);
//        System.out.println(getProfile(userId));
    }

    @EventMapping
    public void handleDefaultEvent(Event event) {
        System.out.println(event);
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) {
        String text = content.getText();
        String userId = event.getSource().getUserId();
        switch (text) {
            case "Image": {
                thankMessage(userId);
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
                String qr2 = QRCodeUrl.netflixPrompPayUrl;
                pushImage(event.getSource().getSenderId(),qr);
                pushImage(event.getSource().getSenderId(),qr2);
                break;
            }
            case "test sticker": {
                pushSticker(event.getSource().getSenderId(),"6632","11825377");
                break;
            }
        }
    }

    // Check message come from expect group or not
    private boolean roomSplitter(@NonNull Event event) {
        String groupId = event.getSource().getSenderId();
        return groupId.equals(netflixRoom) || groupId.equals(youtubeRoom);
    }

    // Push Image Message to UserId,GroupId or RoomId with URL
    public void pushImage(@NonNull String pushId,@NonNull String imageUrl) {
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

    // Push Image Message to UserId,GroupId or RoomId with URL
    public void pushSticker(@NonNull String pushId,@NonNull String packageId,@NonNull String eventId) {
        StickerMessage stickerMessage = new StickerMessage(packageId,eventId);
        PushMessage pushMessage = new PushMessage(pushId,stickerMessage);
        BotApiResponse botApiResponse;
        try {
            botApiResponse = lineMessagingClient.pushMessage(pushMessage).get();
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            return;
        }
        System.out.println("Successfully Pushed Sticker");
    }

    // Reply Text message to sender with ReplyToken
    public void replyText(@NonNull String replyToken,@NonNull TextMessage content) {
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
    public void pushText(@NonNull String pushId,@NonNull TextMessage content) {
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
    private String getProfile(@NonNull String userId) {
        String displayName;
        UserProfileResponse userProfileResponse;
        try {
            userProfileResponse = lineMessagingClient.getProfile(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        displayName = userProfileResponse.getDisplayName();
        System.out.println("User ID: " + userProfileResponse.getUserId() + " Display name: " + userProfileResponse.getDisplayName());
//        pushText(userId,new TextMessage(userId));
        return displayName;
    }

    // Push Thanks <DisplayName>
    private void thankMessage(@NonNull String userId) {
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
