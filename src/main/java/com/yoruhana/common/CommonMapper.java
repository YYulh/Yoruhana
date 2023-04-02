package com.yoruhana.common;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonMapper {
    @Autowired
    SqlSession sqlSession;
    public CommonMapper(){};

    public int insert;

}
