package com.evokly.kafka.connect.mqtt;

import org.apache.avro.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Copyright 2016 Evokly S.A.
 *
 * <p>See LICENSE file for License
 **/
public interface MqttMessageProcessor {

    MqttMessageProcessor process(String topic, MqttMessage message,
                                 Schema valueSchema,
                                 Schema keySchema);

    SourceRecord[] getRecords(String kafkaTopic);
}
