package com.mz.community.Service;

import com.mz.community.dao.neo4jMapper.NeoFollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeoFollowService {
    @Autowired
    private NeoFollowMapper neoFollowMapper;
    public void addFollow(int userId,int entityId){
        neoFollowMapper.insertFollow(userId,entityId);
    }

    public void deleteFollow(int userId,int entityId){
        neoFollowMapper.deleteFollow(userId,entityId);
    }
}
