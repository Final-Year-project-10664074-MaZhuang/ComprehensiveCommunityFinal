package com.mz.community.dao.neo4jMapper;

import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NeoCrawlerDiscussPostMapper {

    int selectMaxPostId();

    int insertCrawler(@Param("discussPostList") List<DiscussPost> discussPostList);

    int insertCrawlerTags(@Param("tagsList") List<Tags> tagsList,String category);

}
