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
}
