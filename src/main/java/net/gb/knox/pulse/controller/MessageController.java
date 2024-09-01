package net.gb.knox.pulse.controller;

import net.gb.knox.pulse.domain.CreateMessage;
import net.gb.knox.pulse.domain.Subscription;
import net.gb.knox.pulse.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class MessageController {

    private final MessageService messageService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/subscribe")
    public Mono<ResponseEntity<Void>> subscribe(@RequestBody Subscription subscription) {
        return Mono.fromRunnable(() -> messageService.subscribe(subscription))
                .doOnSubscribe(s -> logger.info("subscribe(subscription: {}): enter", subscription))
                .doOnSuccess(v -> logger.info("subscribe(): exit"))
                .doOnError(e -> logger.error("subscribe(exception: {} - {}): error", e.getClass().getName(), e.getMessage()))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping("/unsubscribe/{token}")
    public Mono<ResponseEntity<Void>> unsubscribe(@PathVariable("token") String registrationToken) {
        return Mono.fromRunnable(() -> messageService.unsubscribe(registrationToken))
                .doOnSubscribe(s -> logger.info("unsubscribe(token: {}): enter", registrationToken))
                .doOnSuccess(v -> logger.info("unsubscribe(): exit"))
                .doOnError(e -> logger.error("unsubscribe(exception: {} - {}): error", e.getClass().getName(), e.getMessage()))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/subscribe/topic/{topic}")
    public Mono<ResponseEntity<Void>> subscribeToTopic(@PathVariable String topic, @RequestBody Subscription subscription) {
        return Mono
                .fromCallable(() -> {
                    messageService.subscribeToTopic(subscription, topic);
                    return null;
                })
                .doOnSubscribe(s -> logger.info("subscribeToTopic(topic: {}, subscription: {}): enter", topic, subscription))
                .doOnSuccess(v -> logger.info("subscribeToTopic(): exit"))
                .doOnError(e -> logger.error("subscribeToTopic(exception: {} - {}): error", e.getClass().getName(), e.getMessage()))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping("/unsubscribe/{token}/topic/{topic}")
    public Mono<ResponseEntity<Void>> unsubscribeFromTopic(@PathVariable("token") String registrationToken, @PathVariable String topic) {
        return Mono
                .fromCallable(() -> {
                    messageService.unsubscribeFromTopic(registrationToken, topic);
                    return null;
                })
                .doOnSubscribe(s -> logger.info("unsubscribeToTopic(topic: {}, token: {}): enter", topic, registrationToken))
                .doOnSuccess(v -> logger.info("unsubscribeToTopic(): exit"))
                .doOnError(e -> logger.error("unsubscribeToTopic(exception: {} - {}): error", e.getClass().getName(), e.getMessage()))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/send/notification/{to}")
    public Mono<ResponseEntity<Void>> sendNotification(@PathVariable String to, @RequestBody CreateMessage createMessage) {
        return Mono
                .fromCallable(() -> {
                    messageService.sendNotification(to, createMessage);
                    return null;
                })
                .doOnSubscribe(s -> logger.info("sendNotification(to: {}, createMessage: {}): enter", to, createMessage))
                .doOnSuccess(v -> logger.info("sendNotification(): exit"))
                .doOnError(e -> logger.error("sendNotification(exception: {} - {}): error", e.getClass().getName(), e.getMessage()))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/send/notification/topic/{topic}")
    public Mono<ResponseEntity<Void>> sendNotificationToTopic(@PathVariable String topic, @RequestBody CreateMessage createMessage) {
        return Mono
                .fromCallable(() -> {
                    messageService.sendNotificationToTopic(topic, createMessage);
                    return null;
                })
                .doOnSubscribe(s -> logger.info("sendNotificationToTopic(topic: {}, createMessage: {}): enter", topic, createMessage))
                .doOnSuccess(v -> logger.info("sendNotificationToTopic(): exit"))
                .doOnError(e -> logger.error("sendNotificationToTopic(exception: {} - {}): error", e.getClass().getName(), e.getMessage()))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/multicast/notification")
    public Mono<ResponseEntity<Void>> multicastNotification(@RequestBody CreateMessage createMessage) {
        return Mono
                .fromCallable(() -> {
                    messageService.multicastNotification(createMessage);
                    return null;
                })
                .doOnSubscribe(s -> logger.info("multicastNotification(createMessage: {}): enter", createMessage))
                .doOnSuccess(v -> logger.info("multicastNotification(): exit"))
                .doOnError(e -> logger.error("multicastNotification(exception: {} - {}): error", e.getClass().getName(), e.getMessage()))
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}
