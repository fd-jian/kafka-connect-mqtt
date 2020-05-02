FROM gradle:4.8.1-jdk8-alpine
WORKDIR /project
COPY . /project/
# TODO: why root user??
USER root 
RUN gradle build -x test && \
    gradle copyRuntimeLibs && \
    cp /project/build/output/lib/bcpg*.jar /project/build/libs && \
    cp /project/build/output/lib/bcprov*.jar /project/build/libs && \
    cp /project/build/output/lib/bcpkix*.jar /project/build/libs && \
    cp /project/build/output/lib/org.eclipse.paho.client.mqttv*.jar /project/build/libs

VOLUME /jars

CMD ["/project/entrypoint.sh"]

