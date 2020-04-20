package com.mz.community.Service;

import com.mz.community.dao.neo4jMapper.NeoLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeoLikeService {
    @Autowired
    private NeoLikeMapper neoLikeMapper;
    public void addLike(int userId,int entityId){
        neoLikeMapper.insertLike(userId,entityId);
    }

    public void deleteLike(int userId,int entityId){
        neoLikeMapper.deleteLike(userId,entityId);
    }
}
