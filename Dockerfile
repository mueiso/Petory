# 베이스 이미지
FROM amazoncorretto:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 파일만 먼저 복사 (Layer 캐싱 최적화)
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# 의존성만 먼저 다운로드 (캐시 활용 가능)
RUN ./gradlew dependencies --no-daemon

# 소스 코드는 나중에 복사 (코드 변경 시에만 이 Layer부터 재빌드)
COPY src src
RUN ./gradlew build -x test -x check --no-daemon

# JAR 파일 복사 (여러 JAR 파일 처리)
RUN find build/libs -name "*.jar" -not -name "*plain*" -exec cp {} app.jar \;

# 포트 노출
EXPOSE 8080

# 환경변수 설정 (prod 프로파일 활성화)
ENV SPRING_PROFILES_ACTIVE=prod

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]