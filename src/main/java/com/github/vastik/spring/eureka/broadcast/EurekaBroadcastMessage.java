package com.github.vastik.spring.eureka.broadcast;

public class EurekaBroadcastMessage {
    private String channel;
    private Object payload;
    private String user;

    public EurekaBroadcastMessage() {
    }

    public EurekaBroadcastMessage(String channel, Object payload) {
        this.channel = channel;
        this.payload = payload;
    }

    public EurekaBroadcastMessage(String user, String channel, Object payload) {
        this.channel = channel;
        this.payload = payload;
        this.user = user;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
