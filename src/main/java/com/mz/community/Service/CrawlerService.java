package com.mz.community.service;

import com.mz.community.dao.neo4jMapper.NeoCrawlerDiscussPostMapper;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<DiscussPost> getCrawlerFromStackOverFlow(String[] tagsName) {
        boolean start = false;
        int postId = neoCrawlerDiscussPostMapper.selectMaxPostId();
        int count = 1;
        //Setting environment variables
        System.setProperty("webdriver.chrome.driver", CrawlerService.class.getClassLoader().getResource("chromedriver.exe").getPath());
        //Open browser
        WebDriver webDriver = new ChromeDriver();
        List<DiscussPost> discussPostList = new ArrayList<>();
        List<Tags> tagsList = new ArrayList<>();
        //Find related tags
        for (int i = 0; i < tagsName.length; i++) {
            Tags tags = neoDiscussPostMapper.selectTagByTagName(tagsName[i].toLowerCase());
            if (tags != null) {
                continue;
            }
            neoDiscussPostMapper.insertTags(tagsName[i]);
            start = true;
            webDriver.get("https://stackoverflow.com/questions/tagged/" + tagsName[i] + "?tab=votes&pagesize=50");
            if (i == 0) {
                WebElement closeElement = webDriver.findElement(By.xpath("//div[@class='grid--cell']//a[@class='s-btn s-btn__muted s-btn__icon js-notice-close']"));
                closeElement.click();
            }
            //Pages number
            for (int j = 0; j < 10; j++) {
                List<WebElement> questionElements = webDriver.findElements(By.xpath("//div[@class='question-summary']"));
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
                    discussPost.setId(postId + count++);
                    discussPost.setUserId(182);
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
                    discussPostList.add(discussPost);
                }
                webDriver.findElement(By.xpath("//a[contains(text(),'Next')]")).click();
            }
        }
        webDriver.quit();
        if (start) {
            List<DiscussPost> DiscussPostCollect = discussPostList.stream().distinct().collect(Collectors.toList());
            List<Tags> tagCollect = tagsList.stream().distinct().collect(Collectors.toList());
            neoCrawlerDiscussPostMapper.insertCrawler(DiscussPostCollect);
            neoCrawlerDiscussPostMapper.insertCrawlerTags(tagCollect);
            for (DiscussPost discussPost : DiscussPostCollect) {
                neoDiscussPostMapper.insertRelationDiscussPost(182, discussPost.getId(), discussPost.getTagName());
            }
        }
        return discussPostList.stream().distinct().collect(Collectors.toList());
    }
}
