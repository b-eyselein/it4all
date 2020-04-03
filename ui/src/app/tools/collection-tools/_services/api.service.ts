import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {IExercise, IExerciseCollection} from '../../../_interfaces/models';
import {Lesson} from '../../../_interfaces/lesson';
import {ISqlQueryResult} from '../sql/sql-interfaces';

type ILesson = Lesson;

@Injectable()
export class ApiService {

  constructor(private http: HttpClient) {
  }

  static putHttpOptions: { headers: HttpHeaders } = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  private readonly adminBaseUrl = '/api/admin/tools';

  private readonly baseUrl = '/api/tools';


  // Admin

  adminReadCollections(toolId: string): Observable<IExerciseCollection[]> {
    const url = `${this.adminBaseUrl}/${toolId}/readCollections`;

    return this.http.get<IExerciseCollection[]>(url)
      .pipe(catchError(() => of([])));
  }

  adminReadLessons(toolId: string): Observable<ILesson[]> {
    const url = `${this.adminBaseUrl}/${toolId}/readLessons`;

    return this.http.get<ILesson[]>(url)
      .pipe(catchError(() => of([])));
  }

  adminReadExercises(toolId: string, collId: number): Observable<IExercise[]> {
    const url = `${this.adminBaseUrl}/${toolId}/collections/${collId}/readExercises`;

    return this.http.get<IExercise[]>(url)
      .pipe(catchError(() => of([])));
  }

  adminUpsertCollection(collection: IExerciseCollection): Observable<boolean> {
    const url = `${this.adminBaseUrl}/${collection.toolId}/collections/${collection.id}`;

    return this.http.put<boolean>(url, collection)
      .pipe(catchError(() => of(false)));
  }

  adminUpsertExercise(exercise: IExercise): Observable<boolean> {
    const url = `${this.adminBaseUrl}/${exercise.toolId}/collections/${exercise.collectionId}/exercises/${exercise.id}`;

    return this.http.put<boolean>(url, exercise)
      .pipe(catchError(() => of(false)));
  }

  adminUpsertLesson(lesson: ILesson): Observable<boolean> {
    const url = `${this.adminBaseUrl}/${lesson.toolId}/lessons/${lesson.id}`;

    return this.http.put<boolean>(url, lesson)
      .pipe(catchError(() => of(false)));
  }

  // Special methods for certain tools

  getSqlDbSchema(collId: number): Observable<ISqlQueryResult[]> {
    const url = `${this.baseUrl}/sql/${collId}/dbContent`;

    return this.http.get<ISqlQueryResult[]>(url)
      .pipe(catchError(() => of([])));
  }

}
