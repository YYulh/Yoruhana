package com.yoruhana.mapper;

import com.yoruhana.entity.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    int insertJoin(MemberVO vo);
    MemberVO login(MemberVO vo);
    MemberVO checkNick(String mb_nick);

    MemberVO getInfo(int mb_no);

    List<MemberVO> getProfPic(int mb_no);
}
