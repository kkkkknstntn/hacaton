package ru.app.userservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeNotificationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public Mono<Void> sendNotification(Long userId, String type) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("type", type);

        try {
            String jsonPayload = objectMapper.writeValueAsString(message);

            return Mono.fromRunnable(() -> kafkaTemplate.send("user-notifications", jsonPayload));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Mono.error(new RuntimeException("Failed to serialize message to JSON", e));
        }
    }
}
