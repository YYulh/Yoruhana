package com.yoruhana.service.member;

import com.yoruhana.entity.MemberVO;
import com.yoruhana.mapper.MemberMapper;
import com.yoruhana.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;

    @Override
    public int insertJoin(MemberVO vo) {
        return memberMapper.insertJoin(vo);
    }
    @Override
    public MemberVO login(MemberVO vo){ return memberMapper.login(vo);}

    @Override
    public MemberVO checkNick(String mb_nick){ return memberMapper.checkNick(mb_nick);}

    @Override
    public MemberVO checkId(String mb_id){ return memberMapper.checkId(mb_id);}
    @Override
    public MemberVO getInfo(int mb_no){ return memberMapper.getInfo(mb_no);}
    @Override
    public List<MemberVO> getProfPic(int mb_no){return memberMapper.getProfPic(mb_no);
    }
    @Override
    public int mypageUpdate(MemberVO vo){return memberMapper.mypageUpdate(vo);}
}
