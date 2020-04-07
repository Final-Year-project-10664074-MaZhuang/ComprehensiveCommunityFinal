package com.mz.community.dao.neo4jMapper;

import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NeoDiscussPostMapper {

    Tags selectTagByTagName(String tagName);

    List<Tags> selectTags();

    List<Tags> selectAllTags(int offset,int limit);

    List<Tags> selectTagsByDiscussPostId(int postId);

    List<DiscussPost> selectZeroReply(int userId, int offset, int limit);

    List<DiscussPost> selectRelationZeroReply(int userId,int offset, int limit);

    int insertDiscussPost(DiscussPost discussPost);

    int insertTags(String tagName);

    int insertRelationDiscussPost(int userId,int postId,String[] tagName);

    int updateDiscussPostStatus(int postId,int status);

    int updateDiscussPostScore(int postId,double score);

    int selectTagsTagNumber(String tagName);

    int insertVisitSecond(int entityId, int userId, double second);

    double selectVisitSecondByUserId(int userId,int entityId);

    void updateVisitSecond(int entityId, int userId, double second);
}
