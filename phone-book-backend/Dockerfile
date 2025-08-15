ARG JAVA_VERSION=21

FROM registry.access.redhat.com/ubi8/openjdk-${JAVA_VERSION}-runtime

ARG JAR_NAME
ADD target/${JAR_NAME}.jar ./phone-book-backend.jar

ENTRYPOINT ["java", "-jar", "phone-book-backend.jar"]