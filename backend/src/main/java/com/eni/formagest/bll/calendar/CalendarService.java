package com.eni.formagest.bll.calendar;

import com.eni.formagest.dto.calendar.CalendarEntryDto;

import java.util.List;

public interface CalendarService {

    // Cours de la promotion de l'élève + cours suivis à l'unité, sans doublon, triés par date de début
    List<CalendarEntryDto> getCalendar(Long studentId);
}
