package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.dal.training.SectorRepository;
import com.eni.formagest.dto.training.SectorDto;
import com.eni.formagest.mappers.SectorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SectorServiceImpl implements SectorService {

    private final SectorRepository sectorRepository;

    public SectorServiceImpl(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SectorDto> findAll() {
        return SectorMapper.toDtoList(sectorRepository.findAll());
    }

    @Override
    @Transactional
    public SectorDto create(SectorDto dto) {
        String name = dto.getName().strip();

        if (sectorRepository.existsByName(name)) {
            throw new IllegalArgumentException();
        }


        Sector sector = new Sector();
        sector.setName(name);

        Sector savedSector = sectorRepository.save(sector);

        return SectorMapper.toDto(savedSector);
    }

    @Override
    @Transactional
    public SectorDto update(Long id, SectorDto dto) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        String name = dto.getName().strip();

        if (sectorRepository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException();
        }

        sector.setName(name);

        Sector savedSector = sectorRepository.save(sector);

        return SectorMapper.toDto(savedSector);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        sectorRepository.delete(sector);
        sectorRepository.flush();
    }
}
