package com.yoruhana.entity;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
@Data
public class ChatList {
    private int id;
    private int mb_no;
    private String mb_nick_a;
    private String mb_nick_b;
    private Timestamp createdDate;
    private String mb_name_a;
    private String mb_name_b;
    private int chatRead_a;
    private int chatRead_b;
    private String sendTime;
    private String senderName;
    private String senderNick;
    private String users_title;
    private String content;
    private String fileName;
    private int chatRoomRead;
    private Timestamp  updateDate;

    public ChatList() {
        // TODO Auto-generated constructor stub
    }
}

