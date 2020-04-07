package com.mz.community.dao.neo4jMapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NeoCommentMapper {
    int insertComment(int userId, int postId);
    int updateCommentCount(int postId,int commentCount);
}
