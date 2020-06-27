FROM gradle:4.8.1-jdk8-alpine
WORKDIR /project
COPY . /project/
# TODO: why root user??
USER root 
#RUN gradle build -x test -x findBugsMain && \
RUN gradle build -x test && \
    gradle copyRuntimeLibs && \
    cp /project/build/output/lib/*.jar /project/build/libs && \
    mv /project/build/libs /project/entrypoint.sh /tmp && \
    rm -rf /project && \
    mkdir -p /project/build && \
    mv /tmp/libs /project/build &&\
    mv /tmp/entrypoint.sh /project

VOLUME /jars

CMD ["/project/entrypoint.sh"]

