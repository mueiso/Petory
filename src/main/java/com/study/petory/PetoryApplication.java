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
		// SpringApplication.run(PetoryApplication.class, args);
		// ✅ dotenv를 현재 경로에서 강제로 읽도록 설정
		Dotenv dotenv = Dotenv.configure()
			.directory(System.getProperty("user.dir")) // 현재 루트 디렉토리
			.filename(".env")                         // 파일명 명시
			.ignoreIfMissing()
			.load();

		// ✅ 환경변수를 시스템 속성으로 설정
		dotenv.entries().forEach(entry ->
			System.setProperty(entry.getKey(), entry.getValue())
		);

		// ✅ Spring 프로파일 강제 적용
		SpringApplication app = new SpringApplication(PetoryApplication.class);
		app.setDefaultProperties(Collections.singletonMap("spring.profiles.active", "local"));
		app.run(args);
	}

}
