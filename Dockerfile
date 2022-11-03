FROM openjdk:17
WORKDIR /Bot
COPY ./src/ .
RUN javac -cp \* *.java
RUN jar cfm bot.jar manifest.txt *.class *.jar *.txt
CMD ["java", "-jar", "bot.jar"]
