# Mqtt to Apache Kafka Connect 

*Note*: This is a fork of [evokly/kafka-connect-mqtt](https://github.com/evokly/kafka-connect-mqtt)

MQTT Plugin for Kafka Connect. Intended to be built and provided to a Kafka Connect instance.

## Usage

### Docker

* Build image: `docker build -t [IMAGE_NAME] .`
* Run container: `docker run --rm -d -v [JAR_OUT_DIR]:/jars [IMAGE_NAME]`

The container will exit after launch. `[JAR_OUT_DIR]` will contain the built jars that can be provided to Kafka Connect. `[JAR_OUT_DIR]` may be mounted to the jar plugin directory of a Kafka Connect container.

### Gradle

* Build project: `./gradlew clean jar`
  * Output dirs: `./build/libs` (project jar), `./build/output/lib` (external jars))
  * Provide project jar and -- depending on the use case -- other required external jars to Kafka Connect Instance.
* Generate API documentation: `./gradlew javadoc` (output dir `./build/docs/javadoc`)

## License
See [LICENSE](LICENSE) file for License
