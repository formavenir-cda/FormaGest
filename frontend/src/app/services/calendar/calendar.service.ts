import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { CalendarEntry } from '../../models/calendar/calendar-entry.model';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class CalendarService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/me/calendar';

  getCalendar(): Observable<CalendarEntry[]> {
    return this.http.get<CalendarEntry[]>(this.apiUrl);
  }
}
