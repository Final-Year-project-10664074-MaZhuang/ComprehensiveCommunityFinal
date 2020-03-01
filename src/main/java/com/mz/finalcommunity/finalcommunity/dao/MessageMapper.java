package com.mz.finalcommunity.finalcommunity.dao;

import com.mz.finalcommunity.finalcommunity.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //Query the current user's session list,Return only the latest piece of data per session
    List<Message> selectConversations(int userId,int offset,int limit);

    //Query the number of sessions for the current user
    int selectConversationCount(int userId);

    //Query the list of private messages contained in a conversation
    List<Message> selectLetters(String conversationId,int offset,int limit);

    //Query the number of sessions
    int selectLetterCount(String conversationId);

    //Query the number of unread messages
    int selectLetterUnread(int userId,String conversationId);

}
