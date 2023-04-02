package com.yoruhana.entity;

import lombok.Data;

@Data
public class NotificationVO {
    private int notification_no;
    private int mb_no;
    private String notification_title;
    private String notification_content;
    private int notification_stat;
}
