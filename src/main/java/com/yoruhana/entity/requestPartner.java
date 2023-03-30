package com.yoruhana.entity;

import lombok.Data;

import java.util.Date;

@Data
public class requestPartner {
    private int mb_no; //FK
    private int rp_no;
    private String rp_response_id;
    private int rp_stat;
    private Date rp_reqdate;
    private Date rp_resdate;
    private Date rp_udt;

}
