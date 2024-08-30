package net.gb.knox.pulse.service;

import com.google.firebase.messaging.*;
import net.gb.knox.pulse.domain.CreateMessage;
import net.gb.knox.pulse.domain.Subscription;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MessageService {

    private final Set<String> subscribers = new HashSet<>();

    public void subscribe(Subscription subscription) {
        subscribers.add(subscription.registrationToken());
    }

    public void unsubscribe(String registrationToken) {
        subscribers.remove(registrationToken);
    }

    public void sendNotification(String to, CreateMessage createMessage) throws Exception {
        if (!subscribers.contains(to)) {
            throw new Exception("Cannot send notification to unsubscribed recipient: " + to);
        }

        var message = Message.builder().setNotification(Notification.builder()
                        .setTitle(createMessage.title()).setBody(createMessage.description()).build())
                .setToken(to)
                .build();

        FirebaseMessaging.getInstance().send(message);
    }

    public void sendNotificationToTopic(String topic, CreateMessage createMessage) throws FirebaseMessagingException {
        var message = Message.builder().setNotification(Notification.builder().setTitle(createMessage.title())
                        .setBody(createMessage.description()).build())
                .setTopic(topic)
                .build();

        FirebaseMessaging.getInstance().send(message);
    }

    public void multicastNotification(CreateMessage createMessage) throws FirebaseMessagingException {
        var message = MulticastMessage.builder().setNotification(Notification.builder()
                        .setTitle(createMessage.title()).setBody(createMessage.description()).build())
                .addAllTokens(subscribers)
                .build();

        FirebaseMessaging.getInstance().sendEachForMulticast(message);
    }
}
