import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {Lesson} from '../_interfaces/lesson';
import {catchError} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class LessonApiService {

  readonly baseUrl = '/lessonApi';

  constructor(private httpClient: HttpClient) {
  }

  getLessonsForTool(toolId: string): Observable<Lesson[]> {
    return this.httpClient.get<Lesson[]>(`${this.baseUrl}/${toolId}/lessonBasics`)
      .pipe(catchError(() => of([])));
  }

  getLesson(toolId: string, lessonId: number): Observable<Lesson | undefined> {
    return this.httpClient.get<Lesson>(`${this.baseUrl}/${toolId}/lessons/${lessonId}`)
      .pipe(catchError(() => of(undefined)));
  }

}
