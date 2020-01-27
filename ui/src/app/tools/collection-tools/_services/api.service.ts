import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {IExercise, IExerciseCollection, IExerciseMetaData} from '../../../_interfaces/models';
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

  // Loading

  getCollectionCount(toolId: string): Observable<number> {
    const url = `${this.baseUrl}/${toolId}/collectionCount`;

    return this.http.get<number>(url)
      .pipe(catchError(() => of(0)));
  }

  getCollections(toolId: string): Observable<IExerciseCollection[]> {
    const url = `${this.baseUrl}/${toolId}/collections`;

    return this.http.get<IExerciseCollection[]>(url)
      .pipe(catchError(() => of([])));
  }

  getCollection(toolId: string, collId: number): Observable<IExerciseCollection | undefined> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}`;

    // TODO: send current version of exercise if known to only get if different!
    return this.http.get<IExerciseCollection | undefined>(url)
      .pipe(catchError(() => of(undefined)));
  }

  getExerciseMetaDataForTool(toolId: string): Observable<IExerciseMetaData[]> {
    return this.http.get<IExerciseMetaData[]>(`${this.baseUrl}/${toolId}/exerciseMetaData`)
      .pipe(catchError(() => of([])));
  }

  getExerciseMetaDataForCollection(toolId: string, collId: number): Observable<IExerciseMetaData[]> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exerciseMetaData`;

    return this.http.get<IExerciseMetaData[]>(url)
      .pipe(catchError(() => of([])));
  }

  getExercises(toolId: string, collId: number): Observable<IExercise[]> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exercises`;

    // TODO: send current version of known exercises to only get diff!
    return this.http.get<IExercise[]>(url)
      .pipe(catchError(() => of([])));
  }

  getExercise(toolId: string, collId: number, exId: number): Observable<IExercise | undefined> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exercises/${exId}`;

    // TODO: send current version of exercise if known to only get if different!
    return this.http.get<IExercise>(url)
      .pipe(catchError(() => of(undefined)));
  }

  getLessonCount(toolId: string): Observable<number> {
    const url = `${this.baseUrl}/${toolId}/lessonCount`;

    return this.http.get<number>(url)
      .pipe(catchError(() => of(0)));
  }

  getLessons(toolId: string): Observable<ILesson[]> {
    const url = `${this.baseUrl}/${toolId}/lessons`;

    return this.http.get<ILesson[]>(url)
      .pipe(catchError(() => of([])));
  }

  getLesson(toolId: string, lessonId: number): Observable<ILesson | undefined> {
    const url = `${this.baseUrl}/${toolId}/lessons/${lessonId}`;

    return this.http.get<ILesson | undefined>(url)
      .pipe(catchError(() => of(undefined)));
  }

  // Correction

  correctSolution<S, R>(exercise: IExercise, part: string, solution: S): Observable<R | undefined> {
    const url = `${this.baseUrl}/${exercise.toolId}/collections/${exercise.collectionId}/exercises/${exercise.id}/${part}`;

    return this.http.put<R>(url, JSON.stringify(solution), ApiService.putHttpOptions)
      .pipe(catchError(() => of(undefined)));
  }

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
