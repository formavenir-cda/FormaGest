package com.eni.formagest.mappers;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.dto.training.SectorDto;

import java.util.List;
import java.util.stream.Collectors;

public final class SectorMapper {

    private SectorMapper() {
    }

    public static SectorDto toDto(Sector sector) {
        return SectorDto.builder()
                .id(sector.getId())
                .name(sector.getName())
                .trackCount(sector.getTracks().size())
                .build();
    }

    public static List<SectorDto> toDtoList(List<Sector> sectors) {
        return sectors.stream()
                .map(SectorMapper::toDto)
                .collect(Collectors.toList());
    }
}
