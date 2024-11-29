package ru.app.userservice.repository;

import reactor.core.publisher.Flux;
import ru.app.userservice.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByUsername(String username);
    Mono<User> findByVkId(Long vkId);
    Flux<User> findByGenderAndAgeBetween(String gender, Integer minAge, Integer maxAge);
}
