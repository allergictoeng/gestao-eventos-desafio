import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evento, Page } from '../models/evento.model';

@Injectable({ providedIn: 'root' })
export class EventService {
  private http = inject(HttpClient);

  private readonly API = 'http://localhost:8080/api/events';

  listar(page: number, size: number, sort: string): Observable<Page<Evento>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<Page<Evento>>(this.API, { params });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  create(event: Evento): Observable<Evento> {
    return this.http.post<Evento>(this.API, event);
  }

  update(id: number, event: Evento): Observable<Evento> {
    return this.http.put<Evento>(`${this.API}/${id}`, event);
  }

  getById(id: number): Observable<Evento> {
    return this.http.get<Evento>(`${this.API}/${id}`);
  }
}
