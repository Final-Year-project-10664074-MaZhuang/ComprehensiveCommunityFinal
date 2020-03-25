package com.mz.community.actuator;

import com.mz.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    @ReadOperation
    public String checkConnect(){
        try (Connection connection = dataSource.getConnection();){
            return CommunityUtil.getJSONString(0,connection.toString());
        } catch (SQLException e) {
            LOGGER.error("connection fail: "+e.getMessage());
            return CommunityUtil.getJSONString(1,"connection fail");
        }
    }
}
