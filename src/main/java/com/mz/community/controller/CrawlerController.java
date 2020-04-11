package com.mz.community.controller;

import com.mz.community.dao.elasticsearch.DiscussPostRepository;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.dao.neo4jMapper.TagsMapper;
import com.mz.community.entity.Category;
import com.mz.community.entity.Event;
import com.mz.community.entity.Page;
import com.mz.community.entity.Tags;
import com.mz.community.event.EventProducer;
import com.mz.community.util.CommunityConstant;
import com.mz.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
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
    private DiscussPostRepository discussRepository;
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;
    @Autowired
    private TagsMapper tagsMapper;
    @RequestMapping(path = "/deleteAllES",method = RequestMethod.GET)
    public void deleteAllES(){
        discussRepository.deleteAll();
    }

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
        List<Category> categoryList = neoDiscussPostMapper.selectAllCategory();
        model.addAttribute("categoryList",categoryList);
        return "site/admin/addTags";
    }

    @RequestMapping(path = "/addTags",method = RequestMethod.POST)
    @ResponseBody
    public String addTag(String tag,String category){
        if(StringUtils.isBlank(tag) && StringUtils.isBlank(category)){
            throw new IllegalArgumentException("Tags param can not be null");
        }
        String[] tagsArray = tag.split(",");
        Event event = new Event()
                .setTopic(TOPIC_CRAWLER)
                .setTags(tagsArray)
                .setCategory(category);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0, "Add tag successfully");
    }

    @RequestMapping(path = "/addCategory",method = RequestMethod.POST)
    @ResponseBody
    public String addCategory(String category){
        if(StringUtils.isBlank(category)){
            throw new IllegalArgumentException("Tags param can not be null");
        }
        String[] categoryArray = category.split(",");
        try {
            for (String cate : categoryArray) {
                int i = tagsMapper.insertCategory(cate);
            }
            return CommunityUtil.getJSONString(0, "Add category successfully");
        }catch (Exception e){
            return CommunityUtil.getJSONString(1, "Add category failure");
        }
    }
}
