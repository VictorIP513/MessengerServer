package messenger.service;

import com.google.gson.Gson;
import messenger.model.Message;
import messenger.model.NotificationMessage;
import messenger.model.User;
import messenger.properties.ServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@Service
class NotificationService {

    private static final String DEFAULT_PRIORITY = "high";
    private static final String TOPICS_ADDRESS = "/topics/";

    private static final String MESSAGE_DATA_TYPE = "message";
    private static final String NEW_FRIEND_TYPE = "new_friend";
    private static final String ACCEPT_FRIEND_REQUEST_TYPE = "accept_friend_request";

    @Autowired
    private Gson gson;

    void notifyANewMessage(Set<User> usersToSendNotification, Message message) {
        for (User user : usersToSendNotification) {
            sendNotificationMessage(MESSAGE_DATA_TYPE, message, user.getLogin());
        }
    }

    void notifyANewFriend(User user, User friendUser) {
        sendNotificationMessage(NEW_FRIEND_TYPE, user, friendUser.getLogin());
    }

    void notifyAAcceptFriendRequest(User user, User friendUser) {
        sendNotificationMessage(ACCEPT_FRIEND_REQUEST_TYPE, user, friendUser.getLogin());
    }

    private void sendNotificationMessage(String dataKey, Object dataValue, String sendTo) {
        Map<String, Object> data = Collections.singletonMap(dataKey, dataValue);
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setData(data);
        notificationMessage.setPriority(DEFAULT_PRIORITY);
        notificationMessage.setTo(TOPICS_ADDRESS + sendTo);
        String json = gson.toJson(notificationMessage);
        sendNotificationJson(json);
    }

    @Async
    private void sendNotificationJson(String json) {
        HttpEntity<String> request = new HttpEntity<>(json);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization",
                "key=" + ServerProperties.getProperty("firebase.server_key")));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(interceptors);

        String firebaseResponse =
                restTemplate.postForObject(ServerProperties.getProperty("firebase.api_url"), request, String.class);

        CompletableFuture<String> pushNotification = CompletableFuture.completedFuture(firebaseResponse);
        CompletableFuture.allOf(pushNotification).join();
    }
}
