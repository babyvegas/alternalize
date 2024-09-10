package com.debadev.alternalize;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlternalizeApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AlternalizeApplication.class);
		//Obtenemos puerto de Heroku
		String port = System.getenv("PORT");
		if (port != null) {
			app.setDefaultProperties(Collections.singletonMap("server.port", port));
		}
		
		app.run(args);
	}
	


}
