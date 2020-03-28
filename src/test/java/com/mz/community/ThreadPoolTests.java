package com.mz.community;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes =CommunityApplication.class)
public class ThreadPoolTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolTests.class);
    //jdk threadPool
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    //jdk Scheduled thread pool
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    //spring thread pool
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    //spring scheduled thread pool
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //jdk threadPool
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("Hello ExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        sleep(10000);
    }

    //2. Thread pool that can execute timed tasks

    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello ScheduledExecutorService");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MICROSECONDS);

        sleep(30000);
    }

    //3. Spring comment thread pool
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Hello ThreadPoolTaskExecutor");
            }
        };
        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }

    //4. spring Thread pool that can execute timed tasks
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Hello ThreadPoolTaskScheduler");
            }
        };
        Date startTime = new Date(System.currentTimeMillis() + 10000);
        taskScheduler.scheduleAtFixedRate(task, startTime, 5000);
        sleep(30000);
    }
}
