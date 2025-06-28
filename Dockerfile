# 베이스 이미지
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# Gradle과 소스 코드 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# Gradle 빌드 실행 (테스트 및 체크 제외)
RUN ./gradlew build -x test -x check

# JAR 파일 복사 (여러 JAR 파일 처리)
RUN find build/libs -name "*.jar" -not -name "*plain*" -exec cp {} app.jar \;

# 포트 노출
EXPOSE 8080

# 환경변수 설정 (prod 프로파일 활성화)
ENV SPRING_PROFILES_ACTIVE=prod

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]