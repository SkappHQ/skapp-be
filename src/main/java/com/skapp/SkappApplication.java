package com.skapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@SpringBootApplication
@EntityScan(basePackages = { "com.skapp.community.peopleplanner.model", "com.skapp.community.common.model",
		"com.skapp.community.timeplanner.model", "com.skapp.community.leaveplanner.model" })
public class SkappApplication implements AsyncConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SkappApplication.class, args);
	}

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadNamePrefix("Async-Thread-");
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(500);
		executor.initialize();
		return executor;
	}

}
