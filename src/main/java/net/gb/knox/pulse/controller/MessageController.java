package net.gb.knox.pulse.controller;

import net.gb.knox.pulse.domain.CreateMessage;
import net.gb.knox.pulse.domain.Subscription;
import net.gb.knox.pulse.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/subscribe")
    public Mono<ResponseEntity<Void>> subscribe(@RequestBody Subscription subscription) {
        return Mono.fromRunnable(() -> messageService.subscribe(subscription))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping("/unsubscribe/{token}")
    public Mono<ResponseEntity<Void>> unsubscribe(@PathVariable("token") String registrationToken) {
        return Mono.fromRunnable(() -> messageService.unsubscribe(registrationToken))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/send/notification/{to}")
    public Mono<ResponseEntity<Void>> sendNotification(@PathVariable String to, @RequestBody CreateMessage createMessage) {
        return Mono.fromCallable(() -> {
            messageService.sendNotification(to, createMessage);
            return null;
        }).then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/send/notification/topic/{topic}")
    public Mono<ResponseEntity<Void>> sendNotificationToTopic(@PathVariable String topic, @RequestBody CreateMessage createMessage) {
        return Mono.fromCallable(() -> {
            messageService.sendNotificationToTopic(topic, createMessage);
            return null;
        }).then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/multicast/notification")
    public Mono<ResponseEntity<Void>> multicastNotification(@RequestBody CreateMessage createMessage) {
        return Mono.fromCallable(() -> {
            messageService.multicastNotification(createMessage);
            return null;
        }).then(Mono.just(ResponseEntity.ok().build()));
    }
}
