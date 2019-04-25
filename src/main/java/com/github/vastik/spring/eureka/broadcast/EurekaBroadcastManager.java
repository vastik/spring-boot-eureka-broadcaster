package com.github.vastik.spring.eureka.broadcast;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty({"eureka.broadcast.enabled", "eureka.client.enabled"})
public class EurekaBroadcastManager {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${eureka.instance.ip-address}")
    private String selfAddress;

    @Value("${eureka.broadcast.endpoint:/api/eureka/broadcast}")
    private String broadcastEndpoint;

    private final RestTemplate restTemplate;

    private static final Logger log = LogManager.getLogger(EurekaBroadcastManager.class);

    public EurekaBroadcastManager(RestTemplateBuilder builder) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder
                .create()
                .setRedirectStrategy(new DefaultRedirectStrategy());

        CloseableHttpClient httpClient = httpClientBuilder.build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(2000);
        restTemplate = builder.build();
        restTemplate.setRequestFactory(requestFactory);
    }

    @Async
    public void broadcast(EurekaBroadcastMessage message) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<EurekaBroadcastMessage> entity = new HttpEntity<>(message, headers);
        getInstancesUri().forEach(uri -> sendRequest(uri, entity));
    }

    boolean isInstance(String host) {
        return getInstancesUri().stream().anyMatch(uri -> uri.getHost().equals(host));
    }

    private void sendRequest(URI uri, HttpEntity<EurekaBroadcastMessage> entity) {
        String uriString = UriComponentsBuilder.fromUri(uri).path(broadcastEndpoint).toUriString();
        log.debug("Sending to {}, channel {}, user: {}", uriString, entity.getBody().getChannel(), entity.getBody().getUser());

        try {
            restTemplate.exchange(uriString, HttpMethod.POST, entity, Void.class);
        } catch (RestClientException ex) {
            log.warn("Broadcast failed on {}: {}", uriString, ex);
        }
    }

    private List<URI> getInstancesUri() {
        return discoveryClient.getInstances(applicationName).stream()
                .map(ServiceInstance::getUri)
                .filter(uri -> !uri.getHost().equalsIgnoreCase(selfAddress))
                .collect(Collectors.toList());
    }
}
