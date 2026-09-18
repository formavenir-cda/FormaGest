package com.eni.formagest.bll.training;

import com.eni.formagest.dto.training.CohortDto;

import java.util.List;

public interface CohortService {

    String MISSING_TRACK = "track";
    String EMPTY_TRACK = "empty-track";

    List<CohortDto> findAll();

    CohortDto create(CohortDto dto);

    boolean hasInProgressCohortForTrack(Long trackId);

    boolean hasInProgressCohortForCourse(Long courseId);

    /**
     * Recalcule le planning des promotions à venir d’un cursus après une modification
     * de sa composition (réordonnancement, ajout ou retrait d’un cours).
     */
    void recalculateUpcomingCohortsForTrack(Long trackId);
}
