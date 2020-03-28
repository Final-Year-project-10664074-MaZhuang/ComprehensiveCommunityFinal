package com.mz.community;

import com.mz.community.Service.AlphaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes =CommunityApplication.class)
public class TransactionTests {
    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1(){
        Object o = alphaService.save1();
        System.out.println(o);
    }
    @Test
    public void testSave2(){
        Object o = alphaService.save2();
        System.out.println(o);
    }
}
