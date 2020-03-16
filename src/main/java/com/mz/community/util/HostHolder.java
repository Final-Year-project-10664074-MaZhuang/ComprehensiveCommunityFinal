package com.mz.community.util;

import com.mz.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * Equivalent to container
 * Information about users
 * it can replace session
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
