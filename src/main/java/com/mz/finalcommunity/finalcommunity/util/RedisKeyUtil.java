package com.mz.finalcommunity.finalcommunity.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "DAU";
    private static final String PREFIX_POST = "post";


    //Likes of an entity
    //like:entity:entityType:entityId->set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //Likes of an user
    //like:user:userId->int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //follow entity
    //followee:userId:entity->zset(entityId,now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //fans
    //follower:entityType:entityId->zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //Login Verification Code
    public static String getKaptchKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //Login credentials
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //user
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    //daily UV
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    //Interval UV
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    //daily DAU
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    //Interval DAU
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    //Article score
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }
}
