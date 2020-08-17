package com.evokly.kafka.connect.mqtt;

import io.confluent.connect.avro.AvroData;
import io.confluent.connect.avro.AvroDataConfig;
import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Copyright 2016 Evokly S.A.
 *
 * <p>See LICENSE file for License
 **/
public interface MqttMessageProcessor {

    // TODO: This interface takes all arguments that each implementing method needs. This makes the abstraction
    //  through this interface quite useless...
    MqttMessageProcessor process(MqttMessage message,
                                 String kafkaTopic,
                                 String kafkaKey,
                                 Schema valueSchema,
                                 Schema keySchema,
                                 AvroData avroData,
                                 DecoderFactory decoderFactory);

    SourceRecord[] getRecords();
}
