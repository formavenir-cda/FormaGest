package com.eni.formagest.dal.training;

import com.eni.formagest.bo.training.TrackCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrackCourseRepository extends JpaRepository<TrackCourse, Long> {

    List<TrackCourse> findByCourseId(Long courseId);

    List<TrackCourse> findByTrackIdOrderByPositionAsc(Long trackId);

    @Query("""
        select coalesce(max(trackCourse.position), 0)
        from TrackCourse trackCourse
        where trackCourse.track.id = :trackId
        """)
    int findMaxPositionByTrackId(Long trackId);
}
