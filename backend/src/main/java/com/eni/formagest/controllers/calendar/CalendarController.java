package com.eni.formagest.controllers.calendar;

import com.eni.formagest.bll.calendar.CalendarService;
import com.eni.formagest.bo.users.User;
import com.eni.formagest.dto.calendar.CalendarEntryDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/calendar")
    @PreAuthorize("hasRole('STUDENT')")
    public List<CalendarEntryDto> getCalendar(@AuthenticationPrincipal User user) {
        return calendarService.getCalendar(user.getId());
    }
}
