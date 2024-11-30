package ru.app.userservice.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import ru.app.userservice.dto.UserResponseDTO;
import ru.app.userservice.enums.Gender;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RecommendationListener {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, Sinks.One<List<UserResponseDTO>>> responseSinks = new ConcurrentHashMap<>();

    public Mono<List<UserResponseDTO>> getRecommendationResponse(Long userId) {
        Sinks.One<List<UserResponseDTO>> sink = Sinks.one();
        responseSinks.put(userId, sink);
        return sink.asMono();
    }

    @KafkaListener(topics = "recommendation-response", groupId = "user-service")
    public void consumeRecommendationResponse(String message) {
        System.out.println("Полученное сообщение: " + message);
        try {
            Map<String, Object> response = parseMessage(message);
            Long userId = ((Number) response.get("user_id")).longValue();
            List<Map<String, Object>> usersList = (List<Map<String, Object>>) response.get("users_list");

            // Преобразуем список Map в список UserResponseDTO
            List<UserResponseDTO> userResponses = usersList.stream()
                    .map(this::convertToUserResponseDTO)
                    .toList();

            Sinks.One<List<UserResponseDTO>> sink = responseSinks.remove(userId);
            if (sink != null) {
                sink.tryEmitValue(userResponses);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> parseMessage(String message) {
        try {
            return objectMapper.readValue(message, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при разборе сообщения", e);
        }
    }

    private UserResponseDTO convertToUserResponseDTO(Map<String, Object> userMap) {
        return UserResponseDTO.builder()
                .id(((Number) userMap.get("user_id")).longValue())
                .firstName((String) userMap.get("first_name"))
                .lastName((String) userMap.get("last_name"))
                .aboutMe((String) userMap.get("about_me"))
                .selectedInterests(((List<Number>) userMap.get("interests"))
                        .stream().map(Number::longValue).toList())
                .gender(Gender.valueOf((String) userMap.get("gender")))
                .city((String) userMap.get("city"))
                .job((String) userMap.get("job"))
                .education((String) userMap.get("education"))
                .photos((List<String>) userMap.get("photos"))
                .build();
    }
}

