package com.example.seektutorials.ui.chat;

import com.google.firebase.Timestamp;

public class Chat {
    String UID, message;
    Timestamp timestamp;

    public Chat(String UID, String message, Timestamp timestamp) {
        this.UID = UID;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public Chat(){

    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
