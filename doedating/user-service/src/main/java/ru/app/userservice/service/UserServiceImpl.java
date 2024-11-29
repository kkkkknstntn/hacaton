package ru.app.userservice.service;

import ru.app.userservice.dto.InterestResponseDTO;
import ru.app.userservice.dto.UserResponseDTO;
import ru.app.userservice.dto.UserRequestDTO;
import ru.app.userservice.entity.*;
import ru.app.userservice.enums.Gender;
import ru.app.userservice.enums.Theme;
import ru.app.userservice.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.app.userservice.repository.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.app.userservice.service.kafka.CalculateRecommendationProducer;
import ru.app.userservice.service.kafka.LikeNotificationProducer;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final UserInterestRepository userInterestRepository;
    private final UserMapper userMapper;
    private final InterestRepository interestRepository;
    private final UserCoordinatesRepository userCoordinatesRepository;
    private final GeoService geoService;
    private final UserFiltersRepository userFiltersRepository;
    private final LikeRepository likeRepository;
    private final LikeNotificationProducer likeNotificationProducer;
    private final MatchRepository matchRepository;
    private final CalculateRecommendationProducer calculateRecommendationProducer;
    @Override
    public Flux<UserResponseDTO> getList() {
        return userRepository.findAll()
                .flatMap(user -> Mono.zip(
                        photoRepository.findByUserId(user.getId()).map(Photo::getPath).collectList(),
                        userInterestRepository.findByUserId(user.getId()).map(UserInterest::getInterestId).collectList(),
                        (photos, interests) -> userMapper.responseMap(user).toBuilder()
                                .photos(photos)
                                .selectedInterests(interests)
                                .build()
                ));
    }

    @Override
    public Mono<UserResponseDTO> getById(Long id) {
        return userRepository.findById(id)
                .flatMap(user -> Mono.zip(
                        photoRepository.findByUserId(user.getId()).map(Photo::getPath).collectList(),
                        userInterestRepository.findByUserId(user.getId()).map(UserInterest::getInterestId).collectList(),
                        (photos, interests) -> userMapper.responseMap(user).toBuilder()
                                .photos(photos)
                                .selectedInterests(interests)
                                .build()
                ));
    }

    @Override
    public Mono<UserResponseDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> Mono.zip(
                        photoRepository.findByUserId(user.getId()).map(Photo::getPath).collectList(),
                        userInterestRepository.findByUserId(user.getId()).map(UserInterest::getInterestId).collectList(),
                        (photos, interests) -> userMapper.responseMap(user).toBuilder()
                                .photos(photos)
                                .selectedInterests(interests)
                                .build()
                ));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return Mono.when(
                userRepository.deleteById(id),
                photoRepository.deleteByUserId(id),
                userInterestRepository.deleteByUserId(id)
        );
    }
    @Override
    public Mono<UserResponseDTO> update(Long id, UserRequestDTO userDTO) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    String newCity = userDTO.getCity();
                    if (newCity != null && !newCity.equals(existingUser.getCity())) {
                        return geoService.fetchCoordinates(newCity, id)
                                .flatMap(coords -> userCoordinatesRepository.findByUserId(id)
                                        .flatMap(existingCoords -> {
                                            existingCoords.setCity(coords.getCity());
                                            existingCoords.setLatitude(coords.getLatitude());
                                            existingCoords.setLongitude(coords.getLongitude());
                                            return userCoordinatesRepository.save(existingCoords);
                                        })
                                        .switchIfEmpty(Mono.defer(() -> userCoordinatesRepository.save(coords)))
                                        .thenReturn(updateExistingUser(existingUser, userDTO))
                                );
                    }
                    return Mono.just(updateExistingUser(existingUser, userDTO));
                })
                .flatMap(userRepository::save)
                .flatMap(updatedUser -> {
                    return userFiltersRepository.findByUserId(id)
                            .flatMap(existingFilters -> {
                                log.info("Filters already exist for user ID: {}", id);
                                return Mono.just(updatedUser);
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                UserFilters newFilters = new UserFilters();
                                newFilters.setUserId(id);
                                LocalDate birthDate = updatedUser.getBirthDate();
                                if (birthDate != null) {
                                    int age = calculateAge(birthDate);
                                    newFilters.setMinAge(age - 2);
                                    newFilters.setMaxAge(age + 2);
                                }
                                Gender oppositeGender = updatedUser.getGender() == Gender.MALE ? Gender.FEMALE : Gender.MALE;
                                newFilters.setGenderFilter(oppositeGender);
                                newFilters.setSearchRadius(50);

                                return userFiltersRepository.save(newFilters)
                                        .doOnSuccess(savedFilters -> log.info("Filters created for user ID: {}", id))
                                        .thenReturn(updatedUser);
                            }));
                })
                .flatMap(updatedUser -> Mono.zip(
                        updateUserPhotos(updatedUser.getId(), userDTO.getPhotos()),
                        updateUserInterests(updatedUser.getId(), userDTO.getSelectedInterests()),
                        (photos, interests) -> userMapper.responseMap(updatedUser).toBuilder()
                                .photos(photos)
                                .selectedInterests(interests)
                                .theme(updatedUser.getTheme().name())
                                .build()
                ));
    }

    private User updateExistingUser(User existingUser, UserRequestDTO userDTO) {
        LocalDate birthDate = Optional.ofNullable(userDTO.getBirthDate()).orElse(existingUser.getBirthDate());
        int age = calculateAge(birthDate);

        return existingUser.toBuilder()
                .firstName(Optional.ofNullable(userDTO.getFirstName()).orElse(existingUser.getFirstName()))
                .lastName(Optional.ofNullable(userDTO.getLastName()).orElse(existingUser.getLastName()))
                .email(Optional.ofNullable(userDTO.getEmail()).orElse(existingUser.getEmail()))
                .birthDate(birthDate)
                .age(age)
                .gender(Optional.ofNullable(userDTO.getGender()).orElse(existingUser.getGender()))
                .city(Optional.ofNullable(userDTO.getCity()).orElse(existingUser.getCity()))
                .job(Optional.ofNullable(userDTO.getJob()).orElse(existingUser.getJob()))
                .education(Optional.ofNullable(userDTO.getEducation()).orElse(existingUser.getEducation()))
                .aboutMe(Optional.ofNullable(userDTO.getAboutMe()).orElse(existingUser.getAboutMe()))
                .telegramId(Optional.ofNullable(userDTO.getTelegramId()).orElse(existingUser.getTelegramId()))
                .theme(Optional.ofNullable(userDTO.getTheme())
                        .map(Theme::valueOf)
                        .orElse(existingUser.getTheme()))
                .build();
    }

    @Override
    public Flux<InterestResponseDTO> getUserInterests(Long userId) {
        return userRepository.findById(userId)
                .flatMapMany(user -> userInterestRepository.findByUserId(user.getId())
                        .flatMap(userInterest -> interestRepository.findById(userInterest.getInterestId()))
                )
                .map(interest -> new InterestResponseDTO(
                        interest.getName(),
                        interest.getColor(),
                        interest.getTextColor()
                ));
    }

    private Mono<List<String>> updateUserPhotos(Long userId, List<String> newPhotos) {
        if (newPhotos == null || newPhotos.isEmpty()) {
            return photoRepository.findByUserId(userId).map(Photo::getPath).collectList();
        }
        return photoRepository.deleteByUserId(userId)
                .thenMany(Flux.fromIterable(newPhotos)
                        .map(photoPath -> new Photo(null, userId, photoPath)))
                .as(photoRepository::saveAll)
                .then(photoRepository.findByUserId(userId).map(Photo::getPath).collectList());
    }

    private Mono<List<Long>> updateUserInterests(Long userId, List<Long> newInterests) {
        if (newInterests == null || newInterests.isEmpty()) {
            return userInterestRepository.findByUserId(userId).map(UserInterest::getInterestId).collectList();
        }

        return userInterestRepository.deleteByUserId(userId)
                .thenMany(Flux.fromIterable(newInterests)
                        .map(interestId -> new UserInterest(userId, interestId)))
                .as(userInterestRepository::saveAll)
                .then(userInterestRepository.findByUserId(userId).map(UserInterest::getInterestId).collectList());
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    //////////////

    public Mono<UserFilters> getUserFilters(Long userId) {
        return userFiltersRepository.findByUserId(userId);
    }

    public Mono<UserFilters> updateUserFilters(Long userId, UserFilters filters) {
        return userFiltersRepository.findByUserId(userId)
                .flatMap(existingFilters -> {
                    if (filters.getMinAge() != null) {
                        existingFilters.setMinAge(filters.getMinAge());
                    }
                    if (filters.getMaxAge() != null) {
                        existingFilters.setMaxAge(filters.getMaxAge());
                    }
                    if (filters.getGenderFilter() != null) {
                        existingFilters.setGenderFilter(filters.getGenderFilter());
                    }
                    if (filters.getSearchRadius() != null) {
                        existingFilters.setSearchRadius(filters.getSearchRadius());
                    }
                    return userFiltersRepository.save(existingFilters);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    filters.setUserId(userId);
                    return userFiltersRepository.save(filters);
                }));
    }

    /////////////
    public Mono<List<UserResponseDTO>> getCompatibleUsersInfo(Long userId) {
        return userFiltersRepository.findByUserId(userId)
                .flatMap(userFilters -> userCoordinatesRepository.findByUserId(userId)
                        .flatMapMany(currentUserCoord -> userRepository.findByGenderAndAgeBetween(
                                        userFilters.getGenderFilter().name(),
                                        userFilters.getMinAge(),
                                        userFilters.getMaxAge())
                                .flatMap(user -> userCoordinatesRepository.findByUserId(user.getId())
                                        .filter(coord -> coord.getCity() != null &&
                                                coord.getLatitude() != null &&
                                                coord.getLongitude() != null)
                                        .filter(coord -> geoService.calculateDistance(
                                                currentUserCoord.getLatitude(),
                                                currentUserCoord.getLongitude(),
                                                coord.getLatitude(),
                                                coord.getLongitude()) <= userFilters.getSearchRadius())
                                        .flatMap(coord -> Mono.zip(
                                                userInterestRepository.findByUserId(user.getId())
                                                        .map(UserInterest::getInterestId)
                                                        .collectList(),
                                                Mono.just(user)
                                        ))
                                )
                                .map(tuple -> {
                                    List<Long> selectedInterestIds = tuple.getT1();
                                    User matchedUser = tuple.getT2();

                                    return UserResponseDTO.builder()
                                            .id(matchedUser.getId())
                                            .aboutMe(matchedUser.getAboutMe())
                                            .selectedInterests(selectedInterestIds)
                                            .build();
                                }))
                        .collectList()
                );
    }
    // Лайки

    @Override
    public Mono<Void> likeUser(Long firstUserId, Long secondUserId, Integer typeOfLike) {

        return likeRepository.findByFirstUserIdAndSecondUserId(firstUserId, secondUserId)
                .flatMap(existingLike -> {
                    return Mono.error(new IllegalStateException("Уже есть лайк"));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Like like = Like.builder()
                            .firstUserId(firstUserId)
                            .secondUserId(secondUserId)
                            .typeOfLike(typeOfLike)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return likeRepository.save(like);
                }))
                .then(checkMatch(firstUserId, secondUserId, typeOfLike))
                .then(sendNotificationIfNecessary(secondUserId, typeOfLike))
                .doOnError(e -> log.error("Ошибка с лайком", e))
                .then(Mono.empty());
    }


    private Mono<Void> sendNotificationIfNecessary(Long secondUserId, Integer typeOfLike) {
        if (typeOfLike != 1) {
            return Mono.empty();
        }
        return likeNotificationProducer.sendNotification(secondUserId, "like");
    }

    private Mono<Void> checkMatch(Long firstUserId, Long secondUserId, Integer typeOfLike) {
        return likeRepository.findByFirstUserIdAndSecondUserId(secondUserId, firstUserId)
                .flatMap(otherLike -> {
                    if (otherLike.getTypeOfLike() == 1 || otherLike.getTypeOfLike() == 2) {
                        Match match = Match.builder()
                                .userId1(Math.min(firstUserId, secondUserId))
                                .userId2(Math.max(firstUserId, secondUserId))
                                .createdAt(LocalDateTime.now())
                                .build();
                        return matchRepository.save(match)
                                .then(likeNotificationProducer.sendNotification(firstUserId, "match"))
                                .then(likeNotificationProducer.sendNotification(secondUserId, "match"));
                    }
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> startRecommendation(Long userId){
        return calculateRecommendationProducer.sendCalculateStart(userId);
    }


}

