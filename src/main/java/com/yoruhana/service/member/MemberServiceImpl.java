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

    @Override
    public int updateProfile(MemberVO vo){return memberMapper.updateProfile(vo);}
    @Override
    public List<MemberVO> searchPenpalList(MemberVO vo){return memberMapper.searchPenpalList(vo);}
    @Override
    public int getTotal(){return memberMapper.getTotal();}
    @Override
    public MemberVO getInfo_Nick(String mb_nick_a){return memberMapper.getInfo_Nick(mb_nick_a);}
    @Override
    public int getMbSee(int mb_no){return memberMapper.getMbSee(mb_no);};
    @Override
    public void increaseSee(int mb_no,int total){memberMapper.increaseSee(mb_no,total);}
    @Override
    public void loginTime(String mb_no, String loginT){memberMapper.loginTime(mb_no,loginT);}
    @Override
    public void deleteRoom(int id){memberMapper.deleteRoom(id);}
    @Override
    public void insertBlog(int mb_no){memberMapper.insertBlog(mb_no);}
    @Override
    public int lastNo(){return memberMapper.lastNo();}
}
