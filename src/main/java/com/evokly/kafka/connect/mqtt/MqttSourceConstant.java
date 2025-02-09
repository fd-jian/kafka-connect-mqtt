/**
 * Copyright 2016 Evokly S.A.
 * See LICENSE file for License
 **/

package com.evokly.kafka.connect.mqtt;

public class MqttSourceConstant {

    public static final String KAFKA_TOPIC = "kafka.topic";

    public static final String MQTT_CLIENT_ID = "mqtt.client_id";
    public static final String MQTT_CLEAN_SESSION = "mqtt.clean_session";
    public static final String MQTT_CONNECTION_TIMEOUT = "mqtt.connection_timeout";
    public static final String MQTT_KEEP_ALIVE_INTERVAL = "mqtt.keep_alive_interval";
    public static final String MQTT_SERVER_URIS = "mqtt.server_uris";
    public static final String MQTT_TOPIC = "mqtt.topic";
    public static final String MQTT_QUALITY_OF_SERVICE = "mqtt.qos";
    public static final String MQTT_SSL_CA_CERT = "mqtt.ssl.ca_cert";
    public static final String MQTT_SSL_CERT = "mqtt.ssl.cert";
    public static final String MQTT_SSL_PRIV_KEY = "mqtt.ssl.key";
    public static final String MQTT_USERNAME = "mqtt.user";
    public static final String MQTT_PASSWORD = "mqtt.password";

    public static final String MESSAGE_PROCESSOR = "processing.message_processor";
    public static final String KAFKA_KEY_OFFSET = "processing.kafka_key_offset";
    public static final String KAFKA_TOPIC_OFFSET = "processing.kafka_topic_offset";
    public static final String KAFKA_SCHEMA_SUBJECT = "processing.kafka_schema_subject";
    public static final String SCHEMA_REGISTRY_URL = "processing.schema_registry_url";
}
