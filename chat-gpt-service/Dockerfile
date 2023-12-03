FROM openjdk:11

COPY ./ChatGPT.java /app/
COPY ./dependency/* /app/lib/

WORKDIR /app

RUN javac -cp "/app/lib/*" ChatGPT.java && \
    rm ChatGPT.java && \
    ls -la . && \
    ls -la ./lib

ENV CLASSPATH="/app:/app/lib/*"

ENTRYPOINT java com.intersystems.gateway.JavaGateway 55555