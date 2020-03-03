package com.mz.finalcommunity.finalcommunity.dao.elasticsearch;

import com.mz.finalcommunity.finalcommunity.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
