package com.example.acaterminal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DividerMessage implements Message {
    private int id;
    private String content;
    private Date date;

    @Override
    public String getName() {
        return "none";
    }

    @Override
    public int getId() {
        return id;
    }

    public DividerMessage(int id, String content, String dateTimeString) {
        this.id = id;
        this.content = content;
        this.date = parseDateTime(dateTimeString);
    }

    private Date parseDateTime(String dateTimeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            return sdf.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return sdf.format(date);
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getMessageDirection() {
        return "Divider";
    }

    @Override
    public String getAvatar() {
        return "none";
    }

    @Override
    public String getMessageType() {
        return "Text";
    }
}