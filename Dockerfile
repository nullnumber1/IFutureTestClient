FROM openjdk:18

WORKDIR /usr/local/bin/
COPY target/IFutureTestClient-0.0.1-SNAPSHOT.jar ./client.jar

CMD ["java", "-jar", "client.jar"]

LABEL maintainer="nullnumber1"