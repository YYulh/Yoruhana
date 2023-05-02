package com.yoruhana.service.member;


import com.yoruhana.entity.BlogVO;
import java.util.List;

public interface GroundService {
    public List<BlogVO> getPostList();

    public int insertPost(BlogVO vo);
    public int getPostListByMb_no(int mb_no);
    public void insertPostPic(BlogVO vo);
    public List<BlogVO> getImgList();
    public List<BlogVO> getPostLike(int mb_no);
    public BlogVO isLike(int mb_no, int post_no);
    public void insertLike(int mb_no,int post_no);
    public void unLike(int mb_no, int post_no,int post_like);
    public void iLike(int mb_no, int post_no,int post_like);

}
