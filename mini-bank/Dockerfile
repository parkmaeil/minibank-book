# 11.6절 멀티 스테이지 + 레이어 + non-root
# ---- 1단계: 빌드 ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew clean bootJar --no-daemon
RUN java -Djarmode=tools -jar build/libs/*.jar extract --layers --launcher --destination extracted

# ---- 2단계: 실행 ----
FROM eclipse-temurin:21-jre
RUN useradd --system --create-home minibank      # root가 아닌 전용 사용자
USER minibank
WORKDIR /app
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
