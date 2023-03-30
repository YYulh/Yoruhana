package com.yoruhana.service;

import com.yoruhana.entity.MemberVO;
import com.yoruhana.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService{
    @Autowired
    private MemberMapper memberMapper;

    @Override
    public int insertJoin(MemberVO vo) {
        return memberMapper.insertJoin(vo);
    }
}
