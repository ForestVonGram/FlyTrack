package com.flytrack.sof3;

import org.springframework.boot.SpringApplication;

public class TestSof3Application {

	public static void main(String[] args) {
		SpringApplication.from(Sof3Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
