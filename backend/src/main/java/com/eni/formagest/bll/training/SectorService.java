package com.eni.formagest.bll.training;

import com.eni.formagest.dal.training.SectorRepository;
import com.eni.formagest.dto.training.SectorDto;
import com.eni.formagest.mappers.SectorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SectorService {

    private final SectorRepository sectorRepository;

    public SectorService(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    @Transactional(readOnly = true)
    public List<SectorDto> findAll() {
        return SectorMapper.toDtoList(sectorRepository.findAll());
    }
}