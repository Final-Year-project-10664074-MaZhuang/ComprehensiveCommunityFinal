package com.mz.community.dao.neo4jMapper;

import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NeoDiscussPostMapper {

    Tags selectTagByTagName(String tagName);

    List<Tags> selectTags();

    List<Tags> selectTagsByDiscussPostId(int postId);

    int insertDiscussPost(DiscussPost discussPost);

    int insertTags(Tags tags);

    int insertRelationDiscussPost(int userId,int postId,String[] tagName);

    int updateDiscussPostStatus(int postId,int status);

    int updateDiscussPostScore(int postId,double score);

}
