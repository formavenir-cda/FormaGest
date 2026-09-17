package com.eni.formagest.bll.training;

import com.eni.formagest.dto.training.CohortDto;

import java.util.List;

public interface CohortService {

    String MISSING_TRACK = "track";
    String EMPTY_TRACK = "empty-track";

    List<CohortDto> findAll();

    CohortDto create(CohortDto dto);
}
