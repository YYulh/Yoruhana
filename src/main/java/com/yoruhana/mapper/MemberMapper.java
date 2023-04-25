package com.yoruhana.mapper;

import com.yoruhana.entity.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    int insertJoin(MemberVO vo);
    MemberVO login(MemberVO vo);
    MemberVO checkNick(String mb_nick);
    MemberVO checkId(String mb_id);
    MemberVO getInfo(int mb_no);
    List<MemberVO> getProfPic(int mb_no);
    int mypageUpdate(MemberVO vo);
    int updateProfile(MemberVO vo);
    List<MemberVO> searchPenpalList(MemberVO vo);
    int getTotal();
    MemberVO getInfo_Nick(String mb_nick_a);
    int getMbSee(int mb_no);
    void increaseSee(int mb_no, int total);
    void loginTime(String mb_no,String loginT);
    void deleteRoom(int id);
    void insertBlog(int mb_no);
    int lastNo();
}

