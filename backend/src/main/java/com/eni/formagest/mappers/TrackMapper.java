package com.eni.formagest.mappers;

import com.eni.formagest.bo.training.Track;
import com.eni.formagest.dto.training.TrackDto;

import java.util.List;
import java.util.stream.Collectors;

public final class TrackMapper {

    private TrackMapper() {
    }

    public static TrackDto toDto(Track track) {
        return TrackDto.builder()
                .id(track.getId())
                .name(track.getName())
                .sectorId(track.getSector().getId())
                .courses(TrackCourseMapper.toDtoList(track.getCourses()))
                .build();
    }

    public static List<TrackDto> toDtoList(List<Track> tracks) {
        return tracks.stream()
                .map(TrackMapper::toDto)
                .collect(Collectors.toList());
    }
}
