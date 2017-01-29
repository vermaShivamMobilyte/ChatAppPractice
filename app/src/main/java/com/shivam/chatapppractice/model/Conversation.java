package com.shivam.chatapppractice.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shivam on 29-01-2017.
 */

public class Conversation {

    private String conversationId;
    private String user1Id;
    private String user2Id;
    private User user1;
    private User user2;
    private Map<String, Chat> chatList = new HashMap<>();

    public Conversation() {

    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(String user1Id) {
        this.user1Id = user1Id;
    }

    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Map<String, Chat> getChatList() {
        return chatList;
    }

    public void setChatList(Map<String, Chat> chatList) {
        this.chatList = chatList;
    }
}
