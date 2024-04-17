package com.example.acaterminal;

import java.util.Date;

public interface Message {

    String getContent();
    String getMessageType();
    String getMessageDirection();
    String getAvatar();
    Date getDate();
    String getDateString();
    String getName();

    int getId();
}
