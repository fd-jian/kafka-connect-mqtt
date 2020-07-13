package com.evokly.kafka.connect.mqtt.sample;

import com.evokly.kafka.connect.mqtt.MqttMessageProcessor;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Copyright 2016 Evokly S.A.
 *
 * <p>See LICENSE file for License
 **/
public class DumbProcessor implements MqttMessageProcessor {
    private static final Logger log = LoggerFactory.getLogger(DumbProcessor.class);
    private MqttMessage mMessage;
    private String mKafkaKey;
    private String mKafkaTopic;

    @Override
    public MqttMessageProcessor process(MqttMessage message, String kfkTopic, String kfkKey, org.apache.avro.Schema valueSchema, org.apache.avro.Schema keySchema) {
        log.debug("processing data for topic: {}; with message {}", kfkTopic, message);
        this.mKafkaKey = kfkKey;
        this.mMessage = message;
        this.mKafkaTopic = kfkTopic;
        return this;
    }

    @Override
    public SourceRecord[] getRecords() {
        return new SourceRecord[]{new SourceRecord(null, null, mKafkaTopic, null,
                Schema.STRING_SCHEMA, mKafkaKey,
                Schema.BYTES_SCHEMA, mMessage.getPayload())};
    }
}
