package ru.app.userservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.app.userservice.entity.Like;

public interface LikeRepository extends R2dbcRepository<Like, Long> {
    Mono<Like> findByFirstUserIdAndSecondUserId(Long firstUserId, Long secondUserId);
}
