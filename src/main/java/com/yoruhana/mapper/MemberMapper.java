package com.yoruhana.mapper;

import com.yoruhana.entity.MemberVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    int insertJoin(MemberVO vo);


}
