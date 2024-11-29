package ru.app.userservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.app.userservice.entity.Match;

public interface MatchRepository extends R2dbcRepository<Match, Long> {
}
