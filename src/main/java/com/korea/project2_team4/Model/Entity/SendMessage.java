package com.korea.project2_team4.Model.Entity;


public class SendMessage {
    private String content;
    private String receiver;



    public SendMessage() {
    }

    public SendMessage(String content, String receiver) {

        this.content = content;
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


}