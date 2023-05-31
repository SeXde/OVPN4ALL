FROM lsedev/ubuntu-ovpn4all:v1
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/OVPN4ALL-1.3.0.jar
ADD ${JAR_FILE} OVPN4ALL.jar
ENTRYPOINT ["java","-jar","/OVPN4ALL.jar"]
