package com.mz.community.dao.neo4jMapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NeoLikeMapper {
    int insertLike(int userId,int postId);
    int deleteLike(int userId,int postId);
}
