package com.example.acaterminal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RightImageMessage implements Message {
    private int id;
    private String content;
    private String avatar;
    private Date date;
    private String name;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public RightImageMessage(int id, String name, String avatar, String content, String dateTimeString) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
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
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getMessageType() {
        return "Image";
    }

    public String getMessageDirection() {
        return "Right";
    }
}
