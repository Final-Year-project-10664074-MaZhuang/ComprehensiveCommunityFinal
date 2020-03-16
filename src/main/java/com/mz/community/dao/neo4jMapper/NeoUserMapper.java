package com.mz.community.dao.neo4jMapper;

import com.mz.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NeoUserMapper {
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateHeader(int id,String headerUrl);
}