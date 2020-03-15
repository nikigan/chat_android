package com.example.jijagram;

import com.google.gson.Gson;

public class Protocol {
    public static final int USER_STATUS = 1;
    public static final int MESSAGE = 2;
    public static final int USER_NAME = 3;

    static class User {
        private long id;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User() {
        }
    }

    static class UserStatus {
        private User user;
        private boolean connected;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public UserStatus() {
        }
    }

    static class Message {
        public static final int GROUP_CHAT = 1;
        private long senderId;
        private long receiverId = GROUP_CHAT;
        private String messageText;

        public long getSenderId() {
            return senderId;
        }

        public void setSenderId(long senderId) {
            this.senderId = senderId;
        }

        public long getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(long receiverId) {
            this.receiverId = receiverId;
        }

        public String getMessageText() {
            return messageText;
        }

        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }

        public Message(String messageText) {
            this.messageText = messageText;
        }
    }

    static class UserName {
        private String name;

        public UserName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static int getType(String json) {
        if (json == null || json.length() == 0)
            return -1;
        return Integer.parseInt(json.substring(0, 1));
    }

    public static UserStatus unpackStatus(String json) {
        Gson g = new Gson();
        return g.fromJson(json.substring(1), UserStatus.class);
    }

    public static Message unpackMessage(String json) {
        Gson g = new Gson();
        return g.fromJson(json.substring(1), Message.class);
    }

    public static String packMessage(Message message) {
        Gson g = new Gson();
        return MESSAGE + g.toJson(message);
    }

    public static String packName(UserName name) {
        Gson g = new Gson();
        return USER_NAME + g.toJson(name);
    }

}
