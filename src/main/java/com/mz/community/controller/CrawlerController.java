package com.mz.community.controller;

import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.Event;
import com.mz.community.entity.Page;
import com.mz.community.entity.Tags;
import com.mz.community.event.EventProducer;
import com.mz.community.util.CommunityConstant;
import com.mz.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CrawlerController implements CommunityConstant {
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;

    @RequestMapping(path = "/tagManagement",method = RequestMethod.GET)
    public String getTagManagementPage(Model model, Page page){
        List<Tags> tagsList = neoDiscussPostMapper.selectTags();
        page.setRows(tagsList.size());
        page.setPath("/tagManagement");
        List<Tags> selectAllTags = neoDiscussPostMapper.selectAllTags(page.getOffset(), page.getLimit());
        List<Map<String,Object>> tags = new ArrayList<>();
        for (Tags selectAllTag : selectAllTags) {
            Map<String,Object> map = new HashMap<>();
            map.put("tag",selectAllTag);
            int tagsTagNumber = neoDiscussPostMapper.selectTagsTagNumber(selectAllTag.getTagName());
            map.put("tagsTagNumber",tagsTagNumber);
            tags.add(map);
        }
        model.addAttribute("AllTags",tags);
        return "site/admin/addTags";
    }

    @RequestMapping(path = "/addTags",method = RequestMethod.POST)
    @ResponseBody
    public String addTag(String tag){
        if(tag==null){
            throw new IllegalArgumentException("Tags param can not be null");
        }
        String[] tagsArray = tag.split(",");
        Event event = new Event()
                .setTopic(TOPIC_CRAWLER)
                .setTags(tagsArray);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0, "Add tag successfully");
    }
}
