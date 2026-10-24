FROM clojure:temurin-21-tools-deps-bookworm-slim AS build
WORKDIR /app

# Dependency layer: rebuild only when deps.edn changes
COPY deps.edn ./
RUN clojure -P -T:build uber

COPY build.clj ./
COPY src ./src
RUN clojure -T:build uber

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S -G app app
COPY --from=build /app/target/app.jar app.jar

USER app
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
