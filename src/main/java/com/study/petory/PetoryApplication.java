package com.study.petory;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.github.cdimascio.dotenv.Dotenv;

@EnableJpaAuditing
@SpringBootApplication
public class PetoryApplication {

	public static void main(String[] args) {
		// ✅ .env 파일에서 환경변수를 시스템 속성으로 등록
		Dotenv dotenv = Dotenv.configure()
			.directory(System.getProperty("user.dir"))
			.filename(".env")
			.ignoreIfMissing()
			.load();

		dotenv.entries().forEach(entry ->
			System.setProperty(entry.getKey(), entry.getValue())
		);

		// ✅ local 프로파일 명시
		SpringApplication app = new SpringApplication(PetoryApplication.class);
		app.setDefaultProperties(Collections.singletonMap("spring.profiles.active", "local"));
		app.run(args);
	}

}
