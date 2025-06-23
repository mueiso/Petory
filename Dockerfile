# 베이스 이미지
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사 (Spring Boot 빌드 결과물)
COPY build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 환경변수 설정 (prod 프로파일 활성화)
ENV SPRING_PROFILES_ACTIVE=prod

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]