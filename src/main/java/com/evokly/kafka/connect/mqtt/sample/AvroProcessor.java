package com.evokly.kafka.connect.mqtt.sample;

import com.evokly.kafka.connect.mqtt.MqttMessageProcessor;
import io.confluent.connect.avro.AvroData;
import io.confluent.connect.avro.AvroDataConfig;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
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
import java.util.List;
import java.util.Optional;


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

    @Override
    public MqttMessageProcessor process(String topic, MqttMessage message,
                                        org.apache.avro.Schema valueSchema,
                                        org.apache.avro.Schema keySchema) {
        log.debug("processing data for topic: {}; with message {}", topic, message);
        this.mTopic = topic;
        this.mMessage = message;

        //  Struct st = new Struct(schema);
        DecoderFactory df = DecoderFactory.get();
        String payloadString = new String(mMessage.getPayload(), StandardCharsets.UTF_8);

        Decoder dec;

        try {
            dec = df.jsonDecoder(valueSchema, payloadString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GenericDatumReader<GenericRecord> reader =
                new GenericDatumReader<>(valueSchema);

        GenericRecord rec;
        try {
            rec = reader.read(null, dec);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AvroData avroData = new AvroData(new AvroDataConfig.Builder().build());

        this.mValueSchemaAndValue = avroData.toConnectData(valueSchema, rec);

        if (keySchema != null) {

            this.mKeySchemaAndValue = avroData.toConnectData(
                    keySchema,
                    new GenericRecordBuilder(keySchema)
                            .set(keySchema.getFields().get(0), mTopic).build());
        }

        return this;
    }

    @Override
    public SourceRecord[] getRecords(String kafkaTopic) {
        return new SourceRecord[]{
                new SourceRecord(null, null, kafkaTopic, null,
                        // TODO: use avro string for key if key schema is not available
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
