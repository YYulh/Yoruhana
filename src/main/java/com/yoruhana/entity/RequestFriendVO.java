package com.yoruhana.entity;

import lombok.Data;

import java.util.Date;

@Data
public class RequestFriendVO {
    private int rf_bo;
    private int mb_no;
    private String rf_response_id;
    private int rf_stat;
    private Date rf_reqdate;
    private Date rf_resdate;
    private Date rf_update;
}
