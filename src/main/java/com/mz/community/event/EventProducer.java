package com.mz.community.event;

import com.alibaba.fastjson.JSONObject;
import com.mz.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //process event
    public void fireEvent(Event event){
        //Post an event to a specified topic
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
