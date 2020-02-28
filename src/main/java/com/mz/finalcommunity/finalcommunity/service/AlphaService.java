package com.mz.finalcommunity.finalcommunity.service;

import com.mz.finalcommunity.finalcommunity.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){
        System.out.println(" alpha Service");
    }

    @PostConstruct
    public void init(){
        System.out.println("init AS");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("destroy AS");
    }

    public String find(){
        return alphaDao.select();
    }
}
