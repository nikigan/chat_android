package com.example.jijagram;

import android.util.Log;

import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    WebSocketClient client;
    URI address;

    private Boolean isAvailable = false;

    public Boolean getAvailable() {
        return isAvailable;
    }

    private Consumer<Pair<String, String>> messageConsumer;
    private Consumer<Pair<String, String>> privateMessageConsumer;

    private Map<Long, String> nameMap = new ConcurrentHashMap<>();

    public Server(Consumer<Pair<String, String>> messageConsumer, Consumer<Pair<String, String>> privateMessageConsumer) {
        this.messageConsumer = messageConsumer;
        this.privateMessageConsumer = privateMessageConsumer;
    }

    public void connect() {
        try {
            address = new URI("ws://192.168.68.116:8881");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        client = new WebSocketClient(address) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("WSSERVER", "Connected to server " + handshakedata);
                isAvailable = true;
            }

            @Override
            public void onMessage(String message) {
                int type = Protocol.getType(message);
                if (type == Protocol.MESSAGE) {
                    onInMessage(message);
                } else if (type == Protocol.USER_STATUS) {
                    onStatusUpdate(message);
                }
                Log.i("WSSERVER", "Message incoming: " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("WSSERVER", "Connection closed!");
                isAvailable = false;
            }

            @Override
            public void onError(Exception ex) {
                Log.i("WSSERVER", "Error " + ex);
                isAvailable = false;
            }
        };

        client.connect();
    }

    private void onStatusUpdate(String json) {
        Protocol.UserStatus status = Protocol.unpackStatus(json);
        Protocol.User u = status.getUser();

        if (status.isConnected()) {
            nameMap.put(u.getId(), u.getName());
        } else {
            nameMap.remove(u.getId());
        }
    }

    private void onInMessage(String json) {
        Protocol.Message message = Protocol.unpackMessage(json);
        String name = nameMap.get(message.getSenderId());
        if (name == null) {
            name = "Unknown";
        }
        String text = null;
        try {
            text = Crypto.decrypt(message.getMessageText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message.getReceiverId() == Protocol.Message.GROUP_CHAT) {
            messageConsumer.accept(new Pair<>(text, name));
        }
        else {
            privateMessageConsumer.accept(new Pair<>(text, name));
        }
    }

    public void sendMessage(String message) {
        long receiver = Protocol.Message.GROUP_CHAT;
        try {
            message = Crypto.encrypt(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Protocol.Message m = new Protocol.Message(message);
        m.setReceiverId(receiver);
        String json = Protocol.packMessage(m);
        if (client != null || client.isOpen()) {
            client.send(json);
        }
    }

    public void sendName(String name) {
        String json = Protocol.packName(new Protocol.UserName(name));
        if (client != null && client.isOpen()) {
            client.send(json);
        }
    }
}
