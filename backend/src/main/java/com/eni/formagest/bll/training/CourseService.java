package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.dal.training.CourseRepository;
import com.eni.formagest.dal.training.TrackCourseRepository;
import com.eni.formagest.dal.training.TrackRepository;
import com.eni.formagest.dto.training.CourseAssociationDto;
import com.eni.formagest.dto.training.CourseDto;
import com.eni.formagest.mappers.CourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class CourseService {

    public static final String MISSING_TRACK = "track";

    private final CourseRepository courseRepository;
    private final TrackRepository trackRepository;
    private final TrackCourseRepository trackCourseRepository;

    public CourseService(
            CourseRepository courseRepository,
            TrackRepository trackRepository,
            TrackCourseRepository trackCourseRepository) {
        this.courseRepository = courseRepository;
        this.trackRepository = trackRepository;
        this.trackCourseRepository = trackCourseRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseDto> findAll() {
        return CourseMapper.toDtoList(courseRepository.findAll());
    }

    @Transactional
    public CourseDto create(CourseDto dto) {
        String name = dto.getName().strip();

        if (courseRepository.existsByName(name)) {
            throw new IllegalArgumentException();
        }

        Course course = new Course();
        course.setName(name);

        Course savedCourse = courseRepository.save(course);
        syncAssociations(savedCourse, dto.getAssociations());

        return CourseMapper.toDto(savedCourse);
    }

    @Transactional
    public CourseDto update(Long id, CourseDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        String name = dto.getName().strip();

        if (courseRepository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException();
        }

        course.setName(name);

        Course savedCourse = courseRepository.save(course);
        syncAssociations(savedCourse, dto.getAssociations());

        return CourseMapper.toDto(savedCourse);
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        courseRepository.delete(course);
        courseRepository.flush();
    }

    private void syncAssociations(
            Course course,
            List<CourseAssociationDto> associations) {

        List<TrackCourse> currentAssociations =
                trackCourseRepository.findByCourseId(course.getId());

        if (!currentAssociations.isEmpty()) {
            trackCourseRepository.deleteAll(currentAssociations);
            trackCourseRepository.flush();
            course.getTrackCourses().clear();
        }

        Map<Long, CourseAssociationDto> associationsByTrack = new LinkedHashMap<>();
        if (associations != null) {
            associations.stream()
                    .filter(association -> association.getTrackId() != null)
                    .forEach(association ->
                            associationsByTrack.put(association.getTrackId(), association));
        }

        if (associationsByTrack.isEmpty()) {
            return;
        }

        List<TrackCourse> savedAssociations = new ArrayList<>();

        associationsByTrack.values().forEach(association -> {
            Track track = trackRepository.findById(association.getTrackId())
                    .orElseThrow(() -> new NoSuchElementException(MISSING_TRACK));

            int position = association.getPosition() > 0
                    ? association.getPosition()
                    : trackCourseRepository.findMaxPositionByTrackId(track.getId()) + 1;

            savedAssociations.add(trackCourseRepository.save(TrackCourse.builder()
                    .course(course)
                    .track(track)
                    .position(position)
                    .build()));
        });

        course.getTrackCourses().addAll(savedAssociations);
    }
}
