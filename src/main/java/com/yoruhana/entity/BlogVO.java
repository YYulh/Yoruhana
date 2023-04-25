package com.yoruhana.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BlogVO {
    //--개인 블로그--
    private int blog_no;
    private int mb_no;
    private String blog_short;
    private int blog_stat;
    //--블로그 게시물--
    private String mb_file;
    private int post_no;
    private String post_content;
    private int post_origin_re;
    private int post_order_re;
    private int post_layer_re;
    private String post_date;

    //--좋아요--
    private int post_like;

    //--사진
    private int post_p_no;
    private String post_p_file;
}
