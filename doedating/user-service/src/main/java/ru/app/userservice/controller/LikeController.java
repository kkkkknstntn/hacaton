package ru.app.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.app.userservice.service.LikeServiceImpl;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeServiceImpl likeService;
    @PostMapping()
    public Mono<Void> likeUser(
            @RequestHeader(value = "X-User-ID", required = false)
            Long userId,
            @RequestParam
            Long targetUserId,
            @RequestParam
            Integer typeOfLike
    ) {
        return likeService.likeUser(userId, targetUserId, typeOfLike);
    }
}
