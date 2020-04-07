package com.mz.community.service;

import com.mz.community.dao.neo4jMapper.NeoCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeoCommentService {
    @Autowired
    private NeoCommentMapper neoCommentMapper;

    public void addComment(int userId,int postId){
        neoCommentMapper.insertComment(userId,postId);
    }

    public void updateCommentCount(int postId, int commentCount) {
        neoCommentMapper.updateCommentCount(postId,commentCount);
    }
}
