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
   public int mypageUpdate(MemberVO vo);
   public int updateProfile(MemberVO vo);
   public List<MemberVO> searchPenpalList(MemberVO vo);
   public int getTotal();
   public MemberVO getInfo_Nick(String mb_nick_a);
   public int getMbSee(int mb_no);
   public void increaseSee(int mb_no,int total);
   public void loginTime(String mb_no,String loginT);
   public void deleteRoom(int id);
   public void insertBlog(int mb_no);
   public int lastNo();

}


