package com.mz.finalcommunity.finalcommunity;

import com.mz.finalcommunity.finalcommunity.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes =FinalcommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("2686224016@qq.com","TEST","First E-mail");
    }

    @Test
    public void testHTMLMail(){
        Context context = new Context();
        context.setVariable("username","Sunday");
        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("2686224016@qq.com","TEST HTML",content);
    }
}
