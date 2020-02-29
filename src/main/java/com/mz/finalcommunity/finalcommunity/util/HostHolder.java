package com.mz.finalcommunity.finalcommunity.util;

import com.mz.finalcommunity.finalcommunity.entity.User;
import org.springframework.stereotype.Component;

/**
 * replace session object
 */
@Component
public class HostHolder {
    private ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void setUserThreadLocal(User user){
        userThreadLocal.set(user);
    }
    public User getUserThreadLocal(){
        return userThreadLocal.get();
    }

    public void clear(){
        userThreadLocal.remove();
    }
}
