package com.akash.embedqa;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "EmbedQA API",
				version = "1.0.0",
				description = "Professional API Testing Platform - Execute, test, and manage your APIs",
				contact = @Contact(
						name = "Akash",
						email = "akash@gmail.com"
				),
				license = @License(
						name = "MIT License",
						url = "https://opensource.org/licenses/MIT"
				)
		)
)
public class EmbedqaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmbedqaApplication.class, args);
	}

}
