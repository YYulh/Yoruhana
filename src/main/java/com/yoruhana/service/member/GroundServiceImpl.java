package com.yoruhana.service.member;

import com.yoruhana.entity.BlogVO;
import com.yoruhana.mapper.GroundMapper;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroundServiceImpl implements GroundService{
    @Autowired
    private GroundMapper groundMapper;

    @Override
    public List<BlogVO> getPostList(){return groundMapper.getPostList(); }

    @Override
    public int insertPost(BlogVO vo) {
    return groundMapper.insertPost(vo);
    }
    @Override
    public int getPostListByMb_no(int mb_no){return groundMapper.getPostListByMb_no(mb_no);}
    @Override
    public void insertPostPic(BlogVO vo){groundMapper.insertPostPic(vo);}
    @Override
    public List<BlogVO> getImgList(){return groundMapper.getImgList();}
    @Override
    public List<BlogVO> getPostLike(int mb_no){return groundMapper.getPostLike(mb_no);}
    @Override
    public BlogVO isLike(int mb_no, int post_no){return groundMapper.isLike(mb_no,post_no);}
    @Override
    public void insertLike(int mb_no, int post_no){
        groundMapper.insertLike(mb_no,post_no);
        groundMapper.increaseLikeCnt(mb_no,post_no);}
    @Override
    public void unLike(int mb_no, int post_no, int post_like){
        groundMapper.unLike(mb_no,post_no,post_like);
        groundMapper.decreaseLikeCnt(mb_no,post_no);
    }
    @Override
    public void iLike(int mb_no, int post_no, int post_like){
        groundMapper.unLike(mb_no,post_no,post_like);
        groundMapper.increaseLikeCnt(mb_no,post_no);}


}
