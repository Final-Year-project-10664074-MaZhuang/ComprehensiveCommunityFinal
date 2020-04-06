package com.mz.community.dao.mysqlMapper;

import com.mz.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit, int orderMode);

    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);
    int updateCommentCount(int id, int commentCount);
    DiscussPost selectDiscussPostById(int id);
    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);

    int insertDiscussPostList(@Param("discussPostCollect")List<DiscussPost> discussPostCollect);
}
