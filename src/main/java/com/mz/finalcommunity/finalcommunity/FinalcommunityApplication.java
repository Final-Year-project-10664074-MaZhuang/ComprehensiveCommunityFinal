package com.mz.finalcommunity.finalcommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class FinalcommunityApplication {

	@PostConstruct
	public void init(){
		//Solve the problem of netty startup conflict
		//see Netty4Utils.setAvailableProcessors
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}

	public static void main(String[] args) {
		SpringApplication.run(FinalcommunityApplication.class, args);
	}

}
