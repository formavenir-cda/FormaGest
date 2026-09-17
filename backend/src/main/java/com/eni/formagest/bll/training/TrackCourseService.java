package com.eni.formagest.bll.training;

import com.eni.formagest.dto.training.CourseDto;
import com.eni.formagest.dto.training.TrackCourseDto;
import com.eni.formagest.dto.training.TrackCourseOrderDto;

import java.util.List;

public interface TrackCourseService {

    String MISSING_TRACK = "track";
    String MISSING_COURSE = "course";
    String COURSE_USED_IN_COHORT = "course-used-in-cohort";
    String TRACK_USED_IN_COHORT = "track-used-in-cohort";

    CourseDto updateCourseTracks(Long courseId, List<Long> trackIds);

    /**
     * Réordonne tous les cours d’un cursus à partir d’une liste complète.
     */
    List<TrackCourseDto> reorderCourses(Long trackId, List<TrackCourseOrderDto> order);
}
