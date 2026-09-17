package com.eni.formagest.bll.training;

import com.eni.formagest.dto.training.SectorDto;

import java.util.List;

public interface SectorService {

    List<SectorDto> findAll();

    SectorDto create(SectorDto dto);

    SectorDto update(Long id, SectorDto dto);

    void delete(Long id);
}
