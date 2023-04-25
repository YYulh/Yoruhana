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

}
