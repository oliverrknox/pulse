package net.gb.knox.pulse.service;

import com.google.firebase.messaging.*;
import net.gb.knox.pulse.domain.CreateMessage;
import net.gb.knox.pulse.domain.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final Set<String> subscribers = new HashSet<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void subscribe(Subscription subscription) {
        subscribers.add(subscription.registrationToken());
    }

    public void unsubscribe(String registrationToken) {
        subscribers.remove(registrationToken);
    }

    public void subscribeToTopic(Subscription subscription, String topic) throws FirebaseMessagingException {
        logger.info("subscribeToTopic(subscription: {}, topic: {}): enter", subscription.registrationToken(), topic);

        var response = FirebaseMessaging.getInstance().subscribeToTopic(List.of(subscription.registrationToken()), topic);

        if (response.getFailureCount() > 0) {
            logger.info("subscribeToTopic(errors: {}): error", response.getErrors().stream()
                    .map(TopicManagementResponse.Error::getReason).collect(Collectors.joining(", ")));
        } else {
            logger.info("subscribeToTopic(): exit");
        }
    }

    public void unsubscribeFromTopic(String registrationToken, String topic) throws FirebaseMessagingException {
        logger.info("unsubscribeFromTopic(subscription: {}, topic: {}): enter", registrationToken, topic);

        var response = FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(registrationToken), topic);

        if (response.getFailureCount() > 0) {
            logger.info("unsubscribeToTopic(errors: {}): error", response.getErrors().stream()
                    .map(TopicManagementResponse.Error::getReason).collect(Collectors.joining(", ")));
        } else {
            logger.info("unsubscribeToTopic(): exit");
        }
    }

    public void sendNotification(String to, CreateMessage createMessage) throws Exception {
        logger.info("sendNotification(to: {}, createMessage: {}): enter", to, createMessage);

        if (!subscribers.contains(to)) {
            throw new Exception("Cannot send notification to unsubscribed recipient: " + to);
        }

        var message = Message.builder().setNotification(Notification.builder()
                        .setTitle(createMessage.title()).setBody(createMessage.description()).build())
                .setToken(to)
                .build();

        FirebaseMessaging.getInstance().send(message);

        logger.info("sendNotification(): exit");
    }

    public void sendNotificationToTopic(String topic, CreateMessage createMessage) throws FirebaseMessagingException {
        logger.info("sendNotificationToTopic(topic: {}, createMessage: {}): enter", topic, createMessage);

        var message = Message.builder().setNotification(Notification.builder().setTitle(createMessage.title())
                        .setBody(createMessage.description()).build())
                .setTopic(topic)
                .build();

        FirebaseMessaging.getInstance().send(message);

        logger.info("sendNotificationToTopic(): exit");
    }

    public void multicastNotification(CreateMessage createMessage) throws FirebaseMessagingException {
        logger.info("multicastNotification(createMessage: {}): enter", createMessage);

        var message = MulticastMessage.builder().setNotification(Notification.builder()
                        .setTitle(createMessage.title()).setBody(createMessage.description()).build())
                .addAllTokens(subscribers)
                .build();

        FirebaseMessaging.getInstance().sendEachForMulticast(message);

        logger.info("multicastNotification(): exit");
    }
}
