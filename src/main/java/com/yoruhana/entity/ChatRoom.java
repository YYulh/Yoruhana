package com.yoruhana.entity;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class ChatRoom {
    private int id;
    private int mb_no;
    private String mb_nick_a;
    private String mb_nick_b;
    private Timestamp createdDate;
    private String mb_name_a;
    private String mb_name_b;
    private int chatRead_a;
    private int chatRead_b;
    private String last_chat;

    private String sendTime;
    private String senderName;
    private String senderNick;
    private String users_title;
    private String content;
    private String fileName;
    private Date updateDate;

    public ChatRoom(int id, int mb_no, String mb_nick_a, String mb_nick_b, Timestamp createdDate, String mb_name_a, String mb_name_b, int chatRead_a, int chatRead_b, String last_chat, String sendTime, String senderName, String senderNick, String users_title, String content, String fileName, Date updateDate) {
        this.id = id;
        this.mb_no = mb_no;
        this.mb_nick_a = mb_nick_a;
        this.mb_nick_b = mb_nick_b;
        this.createdDate = createdDate;
        this.mb_name_a = mb_name_a;
        this.mb_name_b = mb_name_b;
        this.chatRead_a = chatRead_a;
        this.chatRead_b = chatRead_b;
        this.last_chat = last_chat;
        this.sendTime = sendTime;
        this.senderName = senderName;
        this.senderNick = senderNick;
        this.users_title = users_title;
        this.content = content;
        this.fileName = fileName;
        this.updateDate = updateDate;
    }

    public ChatRoom() {
        // TODO Auto-generated constructor stub
    }
    public ChatRoom(String content, String senderName, String sendTime, String senderNick) {
        this.content = content;
        this.senderName = senderName;
        this.sendTime = sendTime;
        this.senderNick = senderNick;
    }
}
