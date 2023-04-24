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

    private int post_no;
    private String post_title;
    private String post_content;
    private int post_origin_re;
    private int post_order_re;
    private int post_layer_re;
    private Timestamp post_date;

    //--좋아요--
    private int post_like;
}
