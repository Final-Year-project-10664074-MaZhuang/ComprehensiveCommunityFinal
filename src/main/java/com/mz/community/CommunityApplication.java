package com.mz.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@EnableTransactionManagement
@SpringBootApplication
public class CommunityApplication {

	@PostConstruct
	public void init() {
		//Solve the problem of netty startup conflict
		//see Netty4Utils.setAvailableProcessors
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
