package com.mz.community.dao.neo4jMapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NeoFollowMapper {
    int insertFollow(int userId,int entityId);
    int deleteFollow(int userId,int entityId);
}
