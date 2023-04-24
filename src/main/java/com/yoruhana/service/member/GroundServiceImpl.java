package com.yoruhana.service.member;

import com.yoruhana.entity.BlogVO;
import com.yoruhana.mapper.GroundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroundServiceImpl implements GroundService{
    @Autowired
    private GroundMapper groundMapper;

    @Override
    public List<BlogVO> getPostList(){return groundMapper.getPostList(); }
}
