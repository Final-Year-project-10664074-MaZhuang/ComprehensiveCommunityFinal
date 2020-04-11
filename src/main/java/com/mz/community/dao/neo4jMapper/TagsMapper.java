package com.mz.community.dao.neo4jMapper;

import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface TagsMapper {
    List<Tags> selectHotTags();

    List<DiscussPost> selectPostByTag(String tagName,int offset,int limit);

    int selectPostByTagCount(String tagName);

    List<Tags> selectRelatedTags(String tagName);

    List<DiscussPost> selectZeroPostByTag(String tagName, int offset, int limit);

    int insertCategory(String cate);
}
