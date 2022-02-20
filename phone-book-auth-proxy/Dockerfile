ARG JAVA_VERSION=17

FROM registry.access.redhat.com/ubi8/openjdk-${JAVA_VERSION}-runtime
LABEL org.opencontainers.image.authors="cosimo.prete.damiano@gmail.com"

ARG JAR_NAME
ADD target/${JAR_NAME}.jar ./phone-book-auth-proxy.jar

ENTRYPOINT ["java", "-jar", "phone-book-auth-proxy.jar"]