package com.bkc.gblibrary;

import org.springframework.boot.Banner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GblibraryApplication implements CommandLineRunner {
	
	@Autowired
	GblibraryRunner gbRunner;
	
	private @Autowired AutowireCapableBeanFactory beanFactory;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GblibraryApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		gbRunner.set(args);
		gbRunner.run();
	}
	
	@Bean
	public GblibraryRunner getGbrunner(String... args) {
		return new GblibraryRunner();
	}
}
