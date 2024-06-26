package by.plamya.project.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class KafkaConfig {

    // Producer configuration
    // @Bean
    // protected ProducerFactory<String, String> producerFactory() {

    //     Map<String, Object> props = new HashMap<>();
    //     props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    //     props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    //     props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    //     return new DefaultKafkaProducerFactory<>(props);
    // }

    // @Bean
    // protected KafkaTemplate<String, String> kafkaTemplate() {
    //     return new KafkaTemplate<>(producerFactory());
    // }

    // @Bean
    // public NewTopic taskTopic() {
    //     return TopicBuilder.name("topic-auth")
    //             .partitions(1)
    //             .replicas(1)
    //             .build();
    // }
}
