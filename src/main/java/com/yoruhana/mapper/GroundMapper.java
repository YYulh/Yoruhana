package com.yoruhana.mapper;

import com.yoruhana.entity.BlogVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroundMapper {

    List<BlogVO> getPostList();
    int insertPost(BlogVO vo);
    int getPostListByMb_no(int mb_no);
    void insertPostPic(BlogVO vo);
    List<BlogVO> getImgList();
    List<BlogVO> getPostLike(int mb_no);
    BlogVO isLike(int mb_no, int post_no);
    void insertLike(int mb_no, int post_no);
    void unLike(int mb_no, int post_no, int post_like);
    void iLike(int mb_no, int post_no, int post_like);
    void decreaseLikeCnt(int mb_no, int post_no);
    void increaseLikeCnt(int mb_no, int post_no);
}
