package com.mz.community.Service;

import com.mz.community.dao.mysqlMapper.DiscussPostMapper;
import com.mz.community.dao.mysqlMapper.UserMapper;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.dao.neo4jMapper.NeoUserMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.User;
import com.mz.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
public class AlphaService {
    @Autowired
    private NeoUserMapper neoUserMapper;
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
        String[] s = {"JAVA","C++"};
        User user = new User();
        user.setId(30000);
        user.setUsername("Alpha");
        user.setEmail("xxxxx@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/532t.png");
        neoUserMapper.insertUser(user);
        DiscussPost discussPost = new DiscussPost();
        discussPost.setId(60000000);
        discussPost.setUserId(30000);
        discussPost.setTitle("Alpha");
        discussPost.setContent("Alpha Test");
        neoDiscussPostMapper.insertDiscussPost(discussPost);
        Integer.valueOf("xxx");
        return "ok";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);
                neoUserMapper.insertUser(user);
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("Hello beta");
                post.setContent("HELLO WORLD beta");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);
                neoDiscussPostMapper.insertDiscussPost(post);
                Integer.valueOf("vvv");
                return "ok";
            }
        });
    }
}
