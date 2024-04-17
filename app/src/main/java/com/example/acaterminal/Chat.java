package com.example.acaterminal;


import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

import java.time.format.DateTimeFormatter;


public class Chat {
    private int id;
    private String title;
    private LocalDate date;
    private List<Message> messageList;


    public int getId() {
        return id;
    }

    public Chat(int id, String title, String dateString) {
        this.id = id;
        this.title = title;
        this.messageList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.date = LocalDate.parse(dateString, formatter);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}

