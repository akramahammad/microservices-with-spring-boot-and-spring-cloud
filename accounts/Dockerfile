#Start with base image containing java runtime
FROM openjdk:11-slim as build

#Information on who maintains the image
MAINTAINER admin

#Add jar to container
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

#execute application
ENTRYPOINT ["java","-jar","/accounts-0.0.1-SNAPSHOT.jar"]