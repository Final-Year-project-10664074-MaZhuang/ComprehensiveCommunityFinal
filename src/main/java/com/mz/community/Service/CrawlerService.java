package com.mz.community.service;

import com.mz.community.dao.neo4jMapper.NeoCrawlerDiscussPostMapper;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import com.mz.community.util.MailClient;
import com.mz.community.util.RedisKeyUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrawlerService {
    @Autowired
    private NeoCrawlerDiscussPostMapper neoCrawlerDiscussPostMapper;
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MailClient mailClient;

    public List<DiscussPost> getCrawlerFromStackOverFlow(String[] tagsName,String category) {
        String crawlerKey = RedisKeyUtil.getCrawlerKey();
        List<DiscussPost> resultDiscuss = new ArrayList<>();
        if (tagsName == null) {
            throw new IllegalArgumentException("Parameter cannot be empty");
        }
        try {
            boolean start = false;
            //Setting environment variables
            /*System.setProperty("webdriver.chrome.driver", CrawlerService.class.getClassLoader().getResource("chromedriver.exe").getPath());
            WebDriver webDriver = new ChromeDriver();*/
            //System.setProperty("webdriver.chrome.driver", CrawlerService.class.getClassLoader().getResource("chromedriver").getPath());
            ChromeOptions options = new ChromeOptions();
            //chrome install location
            System.setProperty("webdriver.chrome.bin", "/opt/google/chrome/chrome");

            //chromederiver store location
            System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
            //No interface parameters
            options.addArguments("headless");
            //Disable sandbox
            options.addArguments("no-sandbox");
            options.addArguments("disable-gpu");
            //Open browser
            WebDriver webDriver = new ChromeDriver(options);
            //Find related tags
            for (int i = 0; i < tagsName.length; i++) {
                Tags tags = neoDiscussPostMapper.selectTagByTagName(tagsName[i].toLowerCase());
                if (tags != null) {
                    continue;
                }
                neoDiscussPostMapper.insertTags(tagsName[i]);
                webDriver.get("https://stackoverflow.com/questions/tagged/" + tagsName[i] + "?tab=votes&pagesize=50");
                if (i == 0) {
                    WebElement closeElement = webDriver.findElement(By.xpath("//div[@class='grid--cell']//a[@class='s-btn s-btn__muted s-btn__icon js-notice-close']"));
                    closeElement.click();
                }
                //Pages number
                for (int j = 0; j < 10; j++) {
                    List<DiscussPost> discussPostList = new ArrayList<>();
                    List<Tags> tagsList = new ArrayList<>();
                    List<WebElement> questionElements = webDriver.findElements(By.xpath("//div[@class='question-summary']"));
                    if(questionElements.size()!=0){
                        start = true;
                        for (WebElement questionElement : questionElements) {
                            DiscussPost discussPost = new DiscussPost();
                            WebElement stats = questionElement.findElement(By.xpath("div[@class='statscontainer']//div[@class='stats']"));
                            String answered = null;
                            try {
                                answered = stats.findElement(By.xpath("div[@class='status answered-accepted']")).findElement(By.tagName("strong")).getText();
                            } catch (Exception e) {
                                continue;
                            }
                            WebElement questionSummaryElement = questionElement.findElement(By.xpath("div[@class='summary']"));
                            WebElement questionLinkElement = questionSummaryElement.findElement(By.className("question-hyperlink"));
                            WebElement excerpt = questionSummaryElement.findElement(By.className("excerpt"));
                            List<WebElement> tagElements = questionSummaryElement.findElements(By.className("post-tag"));
                            String[] tagNames = new String[tagElements.size()];
                            //discussPost.setId(postId + count++);
                            discussPost.setUserId(3);
                            discussPost.setType(4);
                            discussPost.setStatus(0);
                            discussPost.setLinkUrl(questionLinkElement.getAttribute("href"));
                            discussPost.setTitle(questionLinkElement.getText());
                            discussPost.setContent(excerpt.getText());
                            discussPost.setScore(Double.parseDouble(stats.findElement(By.className("vote-count-post")).getText()));
                            discussPost.setCommentCount(Integer.parseInt(answered));
                            discussPost.setCreateTime(new Date());
                            for (int k = 0; k < tagElements.size(); k++) {
                                Tags tag = new Tags();
                                tag.setTagName(tagElements.get(k).getText().toLowerCase());
                                tagsList.add(tag);
                                tagNames[k] = tag.getTagName();
                            }
                            discussPost.setTagName(tagNames);
                            Long href = redisTemplate.opsForSet().add(crawlerKey, questionLinkElement.getAttribute("href"));
                            if(href!=0){
                                discussPostList.add(discussPost);
                                resultDiscuss.add(discussPost);
                            }
                        }
                        if (start) {
                            List<DiscussPost> DiscussPostCollect = discussPostList.stream().distinct().collect(Collectors.toList());
                            List<Tags> tagCollect = tagsList.stream().distinct().collect(Collectors.toList());
                            if(DiscussPostCollect.size()!=0&&tagCollect.size()!=0){
                                try {
                                    discussPostService.addDiscussPostList(DiscussPostCollect);
                                }catch (Exception e){
                                    sentMail("Crawler article failed to insert(Mysql)","Insert crawler data into database failed","zhuang.ma@students.plymouth.ac.uk");
                                    webDriver.quit();
                                    return null;
                                }
                                try {
                                    neoCrawlerDiscussPostMapper.insertCrawlerTags(tagCollect, category);
                                }catch (Exception e){
                                    sentMail("Crawler tag failed to insert(Neo4j)","Insert crawler data into database failed","zhuang.ma@students.plymouth.ac.uk");
                                    webDriver.quit();
                                    return null;
                                }
                                try {
                                    neoCrawlerDiscussPostMapper.insertCrawler(DiscussPostCollect);
                                }catch (Exception e){
                                    sentMail("Crawler article failed to insert(Neo4j)","Insert crawler data into database failed","zhuang.ma@students.plymouth.ac.uk");
                                    webDriver.quit();
                                    return null;
                                }
                                try {
                                    for (DiscussPost discussPost : DiscussPostCollect) {
                                        neoDiscussPostMapper.insertRelationDiscussPost(3, discussPost.getId(), discussPost.getTagName());
                                    }
                                }catch (Exception e){
                                    sentMail("Crawler article relationship failed to insert(Neo4j)","Insert crawler data into database failed","zhuang.ma@students.plymouth.ac.uk");
                                    webDriver.quit();
                                    return null;
                                }
                            }else {
                                sentMail("Data crawl duplicate", "Data crawl duplicate","zhuang.ma@students.plymouth.ac.uk");
                                webDriver.quit();
                                return null;
                            }
                        }else {
                            sentMail("Please replace the crawler data keywords","No relevant data found","zhuang.ma@students.plymouth.ac.uk");
                            webDriver.quit();
                            return null;
                        }
                        webDriver.findElement(By.xpath("//a[contains(text(),'Next')]")).click();
                    }else {
                        break;
                    }
                }
            }
            webDriver.quit();
        } catch (Exception e) {
            sentMail("Data crawling failed, please check the code and Stack Overflow official website","Data crawl failed","zhuang.ma@students.plymouth.ac.uk");
            return null;
        }
        sentMail("Data crawl completed","Data crawl completed","zhuang.ma@students.plymouth.ac.uk");
        return resultDiscuss.stream().distinct().collect(Collectors.toList());
    }

    private void sentMail(String mailContent, String subject, String emailAddress) {
        Context context = new Context();
        context.setVariable("content", mailContent);
        String content = templateEngine.process("/mail/tagResult", context);
        mailClient.sendMail(emailAddress, subject, content);
    }
}
