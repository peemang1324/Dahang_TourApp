package com.example.sns_project.info;

public class ChatInfo {

    private String sender; // 보내는 사람
    private String receiver; // 받는사람
    private String message;  //메세지 내용
    private boolean isseen; //메세지 확인 여부
    public Object timestamp; //메세지 보낸 시간

    public ChatInfo(String sender, String receiver, String message, boolean isseen, Object timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.timestamp = timestamp;
    }

    public ChatInfo() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}

