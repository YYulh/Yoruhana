package com.yoruhana.service.member;


import com.yoruhana.entity.BlogVO;
import java.util.List;

public interface GroundService {
    public List<BlogVO> getPostList();

    public int insertPost(BlogVO vo);
    public int getPostListByMb_no(int mb_no);
    public void insertPostPic(BlogVO vo);

}
