package com.bkc.gblibrary;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GblibraryApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GblibraryApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		getLibraryRunner(args);
	}
	
	@Bean
	public GblibraryRunner getLibraryRunner(String[] args) {
		return new GblibraryRunner(args);
	}

}
