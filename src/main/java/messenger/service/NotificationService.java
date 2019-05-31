package messenger.service;

import com.google.gson.Gson;
import messenger.model.Message;
import messenger.model.User;
import messenger.model.notification.Notification;
import messenger.model.notification.NotificationMessage;
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
    private static final String NOTIFICATION_MESSAGE_TITLE = "New message";
    private static final String NOTIFICATION_MESSAGE_BODY = "Message";
    @Autowired
    private Gson gson;

    void notifyANewMessage(Set<User> usersToSendNotification, Message message) {
        for (User user : usersToSendNotification) {
            Map<String, Object> data = new HashMap<>();
            data.put("message", message);
            Notification notification = new Notification();
            notification.setBody(NOTIFICATION_MESSAGE_BODY);
            notification.setTitle(NOTIFICATION_MESSAGE_TITLE);
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setData(data);
            notificationMessage.setNotification(notification);
            notificationMessage.setPriority(DEFAULT_PRIORITY);
            notificationMessage.setTo(TOPICS_ADDRESS + user.getLogin());

            String json = gson.toJson(notificationMessage);
            sendNotificationJson(json);
        }
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
