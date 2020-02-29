package com.mz.finalcommunity.finalcommunity;

import com.mz.finalcommunity.finalcommunity.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes =FinalcommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testSensitiveFilter(){
        String text = "fuck you";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
