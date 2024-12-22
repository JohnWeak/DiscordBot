FROM gradle:8.5-jdk21
WORKDIR /Bot
COPY . .
RUN ./gradlew shadowJar
RUN mv ./build/libs/discord-bot-1.0-SNAPSHOT-all.jar ./bot.jar
CMD ["java", "-jar", "bot.jar"]
