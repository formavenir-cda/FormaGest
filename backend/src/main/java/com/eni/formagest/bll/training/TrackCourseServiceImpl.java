package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.dal.training.CourseRepository;
import com.eni.formagest.dal.training.TrackCourseRepository;
import com.eni.formagest.dal.training.TrackRepository;
import com.eni.formagest.dto.training.CourseDto;
import com.eni.formagest.dto.training.TrackCourseDto;
import com.eni.formagest.dto.training.TrackCourseOrderDto;
import com.eni.formagest.mappers.CourseMapper;
import com.eni.formagest.mappers.TrackCourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TrackCourseServiceImpl implements TrackCourseService {

    private final CourseRepository courseRepository;
    private final TrackRepository trackRepository;
    private final TrackCourseRepository trackCourseRepository;
    private final CohortService cohortService;

    public TrackCourseServiceImpl(
            CourseRepository courseRepository,
            TrackRepository trackRepository,
            TrackCourseRepository trackCourseRepository,
            CohortService cohortService) {
        this.courseRepository = courseRepository;
        this.trackRepository = trackRepository;
        this.trackCourseRepository = trackCourseRepository;
        this.cohortService = cohortService;
    }

    @Override
    @Transactional
    public CourseDto updateCourseTracks(Long courseId, List<Long> trackIds) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException(MISSING_COURSE));

        Set<Long> requestedTrackIds = validateTrackIds(trackIds);
        List<TrackCourse> existingTrackCourses =
                trackCourseRepository.findByCourseId(courseId);
        Map<Long, TrackCourse> existingTrackCoursesByTrackId =
                mapByTrackId(existingTrackCourses);
        Set<Long> removedTrackIds = existingTrackCourses.stream()
                .map(trackCourse -> trackCourse.getTrack().getId())
                .filter(trackId -> !requestedTrackIds.contains(trackId))
                .collect(Collectors.toSet());
        Set<Long> changedTrackIds = new HashSet<>(removedTrackIds);
        requestedTrackIds.stream()
                .filter(trackId -> !existingTrackCoursesByTrackId.containsKey(trackId))
                .forEach(changedTrackIds::add);

        existingTrackCourses.stream()
                .filter(trackCourse ->
                        !requestedTrackIds.contains(trackCourse.getTrack().getId())
                )
                .forEach(trackCourseRepository::delete);

        trackCourseRepository.flush();
        removedTrackIds.forEach(this::compactTrackPositions);

        for (Long trackId : requestedTrackIds) {
            if (!existingTrackCoursesByTrackId.containsKey(trackId)) {
                addCourseToTrack(course, trackId);
            }
        }

        trackCourseRepository.flush();
        course.setTrackCourses(trackCourseRepository.findByCourseId(courseId));

        changedTrackIds.forEach(cohortService::recalculateUpcomingCohortsForTrack);

        return CourseMapper.toDto(course);
    }

    /**
     * Réordonne tous les cours d’un cursus à partir d’une liste complète.
     */
    @Override
    @Transactional
    public List<TrackCourseDto> reorderCourses(
            Long trackId,
            List<TrackCourseOrderDto> order) {

        validateTrackExists(trackId);

        List<TrackCourse> trackCourses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(trackId);

        validateCompleteOrder(trackCourses, order);

        Map<Long, TrackCourse> trackCoursesByCourseId =
                mapByCourseId(trackCourses);

        validateCoursesBelongToTrack(trackCoursesByCourseId, order);
        validateUniquePositions(order);
        validateContinuousPositions(order, trackCourses.size());

        moveToTemporaryPositions(trackCourses);
        applyNewPositions(trackCoursesByCourseId, order);
        trackCourseRepository.flush();

        cohortService.recalculateUpcomingCohortsForTrack(trackId);

        return TrackCourseMapper.toDtoList(
                trackCourseRepository.findByTrackIdOrderByPositionAsc(trackId)
        );
    }

    /**
     * Vérifie que le cursus existe avant de manipuler ses cours.
     */
    private void validateTrackExists(Long trackId) {
        if (!trackRepository.existsById(trackId)) {
            throw new NoSuchElementException(MISSING_TRACK);
        }
    }

    /**
     * Vérifie que le front envoie bien tous les cours du cursus.
     */
    private void validateCompleteOrder(
            List<TrackCourse> trackCourses,
            List<TrackCourseOrderDto> order) {

        if (order == null || order.size() != trackCourses.size()) {
            throw new IllegalArgumentException(
                    "La liste des cours est incomplète."
            );
        }
    }

    /**
     * Indexe les associations par identifiant de cours pour les retrouver rapidement.
     */
    private Map<Long, TrackCourse> mapByCourseId(List<TrackCourse> trackCourses) {
        return trackCourses.stream()
                .collect(Collectors.toMap(
                        trackCourse -> trackCourse.getCourse().getId(),
                        trackCourse -> trackCourse
                ));
    }

    private Map<Long, TrackCourse> mapByTrackId(List<TrackCourse> trackCourses) {
        return trackCourses.stream()
                .collect(Collectors.toMap(
                        trackCourse -> trackCourse.getTrack().getId(),
                        Function.identity()
                ));
    }

    private Set<Long> validateTrackIds(List<Long> trackIds) {
        if (trackIds == null) {
            return Set.of();
        }

        if (trackIds.stream().anyMatch(trackId -> trackId == null || trackId <= 0)) {
            throw new IllegalArgumentException("Un cursus est invalide.");
        }

        return new LinkedHashSet<>(trackIds);
    }

    private void addCourseToTrack(Course course, Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new NoSuchElementException(MISSING_TRACK));

        trackCourseRepository.save(TrackCourse.builder()
                .course(course)
                .track(track)
                .position(trackCourseRepository.findMaxPositionByTrackId(trackId) + 1)
                .build());
    }

    private void compactTrackPositions(Long trackId) {
        List<TrackCourse> trackCourses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(trackId);

        moveToTemporaryPositions(trackCourses);

        for (int index = 0; index < trackCourses.size(); index++) {
            trackCourses.get(index).setPosition(index + 1);
        }
    }

    /**
     * Vérifie que chaque cours envoyé appartient bien au cursus concerné.
     */
    private void validateCoursesBelongToTrack(
            Map<Long, TrackCourse> trackCoursesByCourseId,
            List<TrackCourseOrderDto> order) {

        boolean hasUnknownCourse = order.stream()
                .anyMatch(item ->
                        !trackCoursesByCourseId.containsKey(item.getCourseId())
                );

        if (hasUnknownCourse) {
            throw new IllegalArgumentException(
                    "Un cours n’appartient pas à ce cursus."
            );
        }
    }

    /**
     * Empêche deux cours d’un même cursus de recevoir la même position.
     */
    private void validateUniquePositions(List<TrackCourseOrderDto> order) {
        Set<Integer> positions = new HashSet<>();

        boolean hasDuplicatePosition = order.stream()
                .map(TrackCourseOrderDto::getPosition)
                .anyMatch(position -> !positions.add(position));

        if (hasDuplicatePosition) {
            throw new IllegalArgumentException(
                    "Deux cours ne peuvent pas avoir la même position."
            );
        }
    }

    /**
     * Vérifie que les positions forment une suite continue de 1 au dernier rang.
     */
    private void validateContinuousPositions(
            List<TrackCourseOrderDto> order,
            int courseCount) {

        Set<Integer> positions = order.stream()
                .map(TrackCourseOrderDto::getPosition)
                .collect(Collectors.toSet());

        for (int expectedPosition = 1; expectedPosition <= courseCount; expectedPosition++) {
            if (!positions.contains(expectedPosition)) {
                throw new IllegalArgumentException(
                        "Les positions doivent aller de 1 au dernier rang."
                );
            }
        }
    }

    /**
     * Déplace temporairement les positions pour éviter les conflits d’unicité en base.
     */
    private void moveToTemporaryPositions(List<TrackCourse> trackCourses) {
        int temporaryPosition = -1;

        for (TrackCourse trackCourse : trackCourses) {
            trackCourse.setPosition(temporaryPosition--);
        }

        trackCourseRepository.flush();
    }

    /**
     * Applique les nouvelles positions validées sur les associations existantes.
     */
    private void applyNewPositions(
            Map<Long, TrackCourse> trackCoursesByCourseId,
            List<TrackCourseOrderDto> order) {

        order.forEach(item ->
                trackCoursesByCourseId.get(item.getCourseId())
                        .setPosition(item.getPosition())
        );
    }
}
