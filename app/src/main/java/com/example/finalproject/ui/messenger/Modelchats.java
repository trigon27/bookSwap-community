package com.example.finalproject.ui.messenger;

public class Modelchats {
    String profileImageUrl;

    String name;

    String chatKey;

    String ownerUid;

    String messageId;

    String messageType;

    String message;

    String fromUid;

    String toUid;

    long timestamp;

    public Modelchats() {
    }

    public Modelchats(String profileImageUrl, String name, String chatKey, String ownerUid, String messageId, String messageType, String message, String fromUid, String toUid, long timestamp) {
        this.profileImageUrl = profileImageUrl;
        this.name = name;
        this.chatKey = chatKey;
        this.ownerUid=ownerUid;
        this.messageId = messageId;
        this.messageType = messageType;
        this.message = message;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.timestamp = timestamp;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
