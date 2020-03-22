package com.mz.community.controller;

import com.mz.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/reply")
    public void methodWithMessage(Message message){
        //System.out.println(name);
        System.out.println("进入reply");
        System.out.println("SendToUser = "+message.getToId()
        +"FromUser = "+message.getFromId()
        +"Content = "+message.getContent());
        messagingTemplate.convertAndSendToUser(message.getToId()+"","/alone",message);
    }
}
