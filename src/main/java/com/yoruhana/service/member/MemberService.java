package com.yoruhana.service.member;

import com.yoruhana.entity.MemberVO;

import java.util.List;


public interface MemberService{

   public int insertJoin(MemberVO vo);
   public MemberVO login(MemberVO vo);

   public MemberVO checkNick(String mb_nick);
   public MemberVO checkId(String mb_id);
   public MemberVO getInfo(int mb_no);
   public List<MemberVO> getProfPic(int mb_no);
}


