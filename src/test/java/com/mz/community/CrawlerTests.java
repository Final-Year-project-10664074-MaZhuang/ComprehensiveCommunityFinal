package com.mz.community;

import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.service.CrawlerService;
import com.mz.community.service.ElasticSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class CrawlerTests {
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;

    @Test
    public void CrawlerSearchTest(){
        String[] key = {"javascript"};
        List<DiscussPost> crawlerFromStackOverFlow = crawlerService.getCrawlerFromStackOverFlow(key);
        /*for (DiscussPost discussPost : crawlerFromStackOverFlow) {
            elasticSearchService.saveDiscussPost(discussPost);
        }*/
    }
}
