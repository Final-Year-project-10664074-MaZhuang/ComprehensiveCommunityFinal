package com.mz.finalcommunity.finalcommunity;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
@SpringBootTest
@ContextConfiguration(classes =FinalcommunityApplication.class)
public class LoggerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTest.class);
    @Test
    public void testLogger(){
        System.out.println(LOGGER.getName());
        LOGGER.debug("debug logger");
        LOGGER.info("info logger");
        LOGGER.warn("warn logger");
        LOGGER.error("error logger");
    }
}
