package com.evokly.kafka.connect.mqtt.sample;

import com.evokly.kafka.connect.mqtt.MqttMessageProcessor;
import io.confluent.connect.avro.AvroData;
import io.confluent.connect.avro.AvroDataConfig;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Copyright 2016 Evokly S.A.
 *
 * <p>See LICENSE file for License
 **/
public class AvroProcessor implements MqttMessageProcessor {
    private static final Logger log = LoggerFactory.getLogger(AvroProcessor.class);
    private MqttMessage mMessage;
    private String mTopic;
    private SchemaAndValue mValueSchemaAndValue;
    private SchemaAndValue mKeySchemaAndValue;
    private AvroData avroData = new AvroData(new AvroDataConfig.Builder().build());
    private DecoderFactory decoderFactory = DecoderFactory.get();
    private Decoder decoder;
    private Object genericDatum;

    @Override
    public MqttMessageProcessor process(String topic,
                                        MqttMessage message,
                                        int mqttTopicOffset,
                                        org.apache.avro.Schema valueSchema,
                                        org.apache.avro.Schema keySchema) {
        log.debug("processing data for topic: {}; with message {}", topic, message);

        this.mTopic = Optional.of(topic)
                .map(s -> s.split("/"))
                .map(s -> s[s.length - mqttTopicOffset - 1])
                .get();

        this.mMessage = message;

        //  Struct st = new Struct(schema);
        String payloadString = new String(mMessage.getPayload(), StandardCharsets.UTF_8);


        this.mValueSchemaAndValue = getSchemaAndValue(valueSchema, payloadString, true);

        this.mKeySchemaAndValue = Optional.ofNullable(keySchema)
                .map(schema -> getSchemaAndValue(schema, mTopic, false))
                .orElse(null);

        return this;
    }

    private SchemaAndValue getSchemaAndValue(org.apache.avro.Schema schema, String value, boolean isValue) {
        try {
            decoder = decoderFactory.jsonDecoder(schema, !isValue && new HashSet<>(
                    Arrays.asList(
                            org.apache.avro.Schema.Type.STRING,
                            org.apache.avro.Schema.Type.BYTES,
                            org.apache.avro.Schema.Type.ENUM,
                            org.apache.avro.Schema.Type.FIXED))
                    .contains(schema.getType())
                    ? String.format("\"%s\"", value)
                    : value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GenericDatumReader<Object> reader =
                new GenericDatumReader<>(schema);

        try {
            genericDatum = reader.read(null, decoder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return avroData.toConnectData(schema, genericDatum);
    }

    @Override
    public SourceRecord[] getRecords(String kafkaTopic) {
        return new SourceRecord[]{
                new SourceRecord(null, null, kafkaTopic, null,
                        Optional.ofNullable(mKeySchemaAndValue)
                                .map(SchemaAndValue::schema)
                                .orElse(Schema.STRING_SCHEMA),
                        Optional.ofNullable(mKeySchemaAndValue)
                                .map(SchemaAndValue::value)
                                .orElse(mTopic),
                        mValueSchemaAndValue.schema(),
                        mValueSchemaAndValue.value())
        };
    }
}
