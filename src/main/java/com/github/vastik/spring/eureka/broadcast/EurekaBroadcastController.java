package com.github.vastik.spring.eureka.broadcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${eureka.broadcast.endpoint}")
@ConditionalOnProperty({"eureka.broadcast.enabled", "eureka.client.enabled"})
public class EurekaBroadcastController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EurekaBroadcastManager manager;

    private static final Logger log = LogManager.getLogger(EurekaBroadcastManager.class);

    @PostMapping
    public void broadcast(@RequestBody EurekaBroadcastMessage message, HttpServletRequest request) {
        if (!manager.isInstance(request.getRemoteHost())) {
            log.warn("Request is not coming from known instances: {}", request.getRemoteHost());
            return;
        }

        log.debug("Sending to {}, user: {}", message.getChannel(), message.getUser());

        if (message.getUser() != null)
            messagingTemplate.convertAndSendToUser(message.getUser(), message.getChannel(), message.getPayload());
        else
            messagingTemplate.convertAndSend(message.getChannel(), message.getPayload());
    }
}
