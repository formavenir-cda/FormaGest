import { Component, OnInit, computed, inject, signal } from '@angular/core';
import type { CalendarOptions } from '@fullcalendar/angular';
import { FullCalendarModule } from '@fullcalendar/angular';
import dayGridPlugin from '@fullcalendar/angular/daygrid';
import themePlugin from '@fullcalendar/angular/themes/classic';
import frLocale from 'fullcalendar/locales/fr';

import { CalendarService } from '../../services/calendar/calendar.service';
import type { CalendarEntry } from '../../models/calendar/calendar-entry.model';

@Component({
  selector: 'app-calendar',
  imports: [FullCalendarModule],
  templateUrl: './calendar.html',
  styleUrl: './calendar.scss',
})
export class CalendarPage implements OnInit {
  private readonly calendarService = inject(CalendarService);

  readonly entries = signal<CalendarEntry[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly calendarOptions = computed<CalendarOptions>(() => ({
    plugins: [themePlugin, dayGridPlugin],
    initialView: 'dayGridMonth',
    locale: frLocale,
    height: 'auto',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: '',
    },
    // Lecture seule :
    editable: false,
    selectable: false,
    events: this.entries().flatMap((entry) =>
      weekdayRanges(entry.startDate, entry.endDate).map((range, index) => ({
        id: `${entry.scheduledCourseId}-${index}`,
        title: entry.courseName,
        start: range.start,
        end: range.end,
        allDay: true,
      }))
    ),
  }));

  ngOnInit(): void {
    this.calendarService.getCalendar().subscribe({
      next: (entries) => {
        this.entries.set(entries);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger le calendrier.');
        this.loading.set(false);
      },
    });
  }
}

function weekdayRanges(startDate: string, endDate: string): { start: string; end: string }[] {
  const ranges: { start: string; end: string }[] = [];
  const last = parseIsoDate(endDate);

  let cursor = parseIsoDate(startDate);
  let rangeStart: Date | null = null;

  while (cursor <= last) {
    if (!isWeekend(cursor) && rangeStart === null) {
      rangeStart = cursor;
    }

    const next = addDays(cursor, 1);
    const closesRange = !isWeekend(cursor) && (next > last || isWeekend(next));

    if (closesRange) {
      ranges.push({ start: formatIsoDate(rangeStart!), end: formatIsoDate(next) });
      rangeStart = null;
    }

    cursor = next;
  }

  return ranges;
}

function parseIsoDate(isoDate: string): Date {
  const [year, month, day] = isoDate.split('-').map(Number);
  return new Date(Date.UTC(year, month - 1, day));
}

function formatIsoDate(date: Date): string {
  return date.toISOString().slice(0, 10);
}

function addDays(date: Date, days: number): Date {
  return new Date(date.getTime() + days * 24 * 60 * 60 * 1000);
}

function isWeekend(date: Date): boolean {
  const day = date.getUTCDay();
  return day === 0 || day === 6;
}
