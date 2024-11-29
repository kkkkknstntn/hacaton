package ru.app.userservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.app.userservice.entity.Like;
import ru.app.userservice.entity.Match;

public interface LikeRepository extends R2dbcRepository<Like, Long> {
    Mono<Like> findByFirstUserIdAndSecondUserId(Long firstUserId, Long secondUserId);

    @Query("SELECT * FROM likes WHERE :user_id1 = secondUserId AND :typeOfLike = 1")
    Flux<Like> findAllBySecondUserId(@Param("user_id2")  Long userId2);

    Flux<Like> findAllLikesForUserId1(@Param("user_id1")  Long userId2);
}
