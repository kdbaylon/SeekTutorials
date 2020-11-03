package com.example.seektutorials.ui.chat;

public class Message {
    String UID;
    public Message(){

    }
    public Message(String UID) {
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
