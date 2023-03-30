package com.yoruhana.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MemberVO {

    //멤버 정보
    private int mb_no;
    private String mb_name;
    private String mb_email1;
    private String mb_email2;
    private String mb_pw;
    private String mb_tel1;
    private String mb_tel2;
    private String mb_tel3;
    private int mb_stat;
    private String mb_prof_pic;
    private String mb_prof_backPic;
    private String mb_prof_content;
    private Date mb_join_date;

    //파트너 정보 슬프게도 null 허용 가능

    private String mb_ptn_email;
    private String mb_ptn_name;
    private String mb_ptn_nick;
    private Date mb_ptn_con_date;


}
