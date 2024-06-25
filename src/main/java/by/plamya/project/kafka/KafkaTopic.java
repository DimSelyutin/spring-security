package by.plamya.project.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class KafkaTopic {

    protected NewTopic topic1() {
        log.info("Created topic");
        return TopicBuilder.name("topic-auth").build();
    }

}