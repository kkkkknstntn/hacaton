package ru.app.userservice.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import ru.app.userservice.dto.InterestResponseDTO;
import ru.app.userservice.dto.UserRequestDTO;
import ru.app.userservice.dto.UserResponseDTO;
import ru.app.userservice.entity.UserFilters;
import ru.app.userservice.service.UserPhotoService;
import ru.app.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserPhotoService userPhotoService;
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<UserResponseDTO> getAllUsers() {
        return userService.getList();
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя с указанным ID.")
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя с указанным ID.")
    @DeleteMapping("/{id}")

    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.delete(id);
    }

    @Operation(summary = "Получить информацию о текущем пользователе",
            description = "Возвращает информацию о аутентифицированном пользователе.")
    @GetMapping("/info")
    public Mono<UserResponseDTO> getUserInfo(
            @RequestHeader(value = "X-User-ID", required = false) Long userId)
    {
        return userService.getById(userId);
    }
    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновляет данные пользователя с указанным ID.")
    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userDTO) {
        return userService.update(id, userDTO);
    }

    // ИНТЕРЕСЫ

    @Operation(
            summary = "Получить список интересов пользователя",
            description = "Возвращает список интересов пользователя с полной информацией.")
    @GetMapping("/interests/{userId}")
    public Flux<InterestResponseDTO> getUserInterests(@PathVariable Long userId) {
        return userService.getUserInterests(userId);
    }

    // ФОТОГРАФИИ

    @PostMapping("/{userId}/photo")
    public Mono<ResponseEntity<Map<String, Object>>> uploadUserPhoto(
            @PathVariable Long userId,
            @RequestPart("file") FilePart filePart) {
        return userPhotoService.processUserPhoto(userId, filePart)
                .map(response -> ResponseEntity.ok().body(response))
                .onErrorResume(e -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(errorResponse));
                });
    }

    @GetMapping("/{userId}/photo/{filename}") // переделать на id
    public Mono<ResponseEntity<Resource>> getUserPhoto(
            @PathVariable Long userId,
            @PathVariable String filename) {
        return userPhotoService.loadUserPhoto(userId, filename)
                .map(resource -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource));
    }

    @DeleteMapping("/{userId}/photo/{filename}")
    public Mono<ResponseEntity<Void>> deleteUserPhoto(
            @PathVariable Long userId,
            @PathVariable String filename) {
        return userPhotoService.deleteUserPhoto(userId, filename)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    // ФИЛЬТРЫ

    @Operation(
            summary = "Получить фильтры пользователя",
            description = "Возвращает фильтры пользователя по его ID.")
    @GetMapping("/filters")
    public Mono<UserFilters> getUserFilters(@RequestHeader(value = "X-User-ID", required = false) Long userId) {
        return userService.getUserFilters(userId);
    }

    @Operation(
            summary = "Обновить фильтры пользователя",
            description = "Изменяет фильтры пользователя по его ID.")
    @PatchMapping("/filters")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserFilters> updateUserFilters(
            @RequestHeader(value = "X-User-ID", required = false) Long userId, @RequestBody UserFilters filters) {
        return userService.updateUserFilters(userId, filters);
    }

    // Начать рекомендации
    @PostMapping("/recommendation")
    public Mono<List<UserResponseDTO>> postRecommendation(@RequestHeader(value = "X-User-ID", required = false) Long userId) {
        return userService.startRecommendation(userId);
    }

//    @GetMapping("/{userId}/recommendation")
//    public Mono<List<UserResponseDTO>> getRecommendation(@PathVariable Long userId) {
//        return userService.getRecommendation(userId);
//    }

}
