package com.mz.finalcommunity.finalcommunity.controller;

import com.mz.finalcommunity.finalcommunity.entity.Message;
import com.mz.finalcommunity.finalcommunity.entity.Page;
import com.mz.finalcommunity.finalcommunity.entity.User;
import com.mz.finalcommunity.finalcommunity.service.MessageService;
import com.mz.finalcommunity.finalcommunity.service.UserService;
import com.mz.finalcommunity.finalcommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //Private message list
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUserThreadLocal();
        //comment pagination
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRow(messageService.findConversationCount(user.getId()));
        //session list
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList!=null){
            for (Message message: conversationList) {
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnread(user.getId(),message.getConversationId()));
                int targetId = user.getId()==message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        //Query the number of unread messages
        int letterUnreadCount = messageService.findLetterUnread(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page,Model model){
        //comment pagination
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRow(messageService.findLetterCount(conversationId));

        //message list
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList!=null){
            for (Message message: letterList) {
                Map<String,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        //query target
        model.addAttribute("target",getLetterTarget(conversationId));

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if(hostHolder.getUserThreadLocal().getId()==id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }
}
