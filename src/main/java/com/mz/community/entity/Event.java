package com.mz.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String topic;
    private int userId;
    private int entityType;
    private int entityId;
    private int entityUserId;
    private String[] tags;
    private int likeStatus;
    private int status;
    private double second;
    private int commentCount;

    public int getCommentCount() {
        return commentCount;
    }

    public Event setCommentCount(int commentCount) {
        this.commentCount = commentCount;
        return this;
    }

    public double getSecond() {
        return second;
    }

    public Event setSecond(double second) {
        this.second = second;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Event setStatus(int status) {
        this.status = status;
        return this;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public Event setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
        return this;
    }

    public String[] getTags() {
        return tags;
    }

    public Event setTags(String[] tags) {
        this.tags = tags;
        return this;
    }
    private Map<String,Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
