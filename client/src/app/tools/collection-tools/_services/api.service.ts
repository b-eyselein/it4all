import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Exercise, ExerciseBasics, ExerciseCollection} from '../../../_interfaces/tool';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';

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

  getCollections(toolId: string): Observable<ExerciseCollection[]> {
    const url = `${this.baseUrl}/${toolId}/collections`;

    return this.http.get<ExerciseCollection[]>(url)
      .pipe(catchError(() => of([])));
  }

  getCollection(toolId: string, collId: number): Observable<ExerciseCollection | undefined> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}`;

    // TODO: send current version of exercise if known to only get if different!
    return this.http.get<ExerciseCollection | undefined>(url)
      .pipe(catchError(() => of(undefined)));
  }

  getExerciseBasics(toolId: string, collId: number): Observable<ExerciseBasics[]> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exerciseBasics`;

    return this.http.get<ExerciseBasics[]>(url)
      .pipe(catchError(() => of([])));
  }

  getExercises<E extends Exercise>(toolId: string, collId: number): Observable<E[]> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exercises`;

    // TODO: send current version of known exercises to only get diff!
    return this.http.get<E[]>(url)
      .pipe(catchError(() => of([])));
  }

  getExercise<E extends Exercise>(toolId: string, collId: number, exId: number): Observable<E> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exercises/${exId}`;

    // TODO: send current version of exercise if known to only get if different!
    return this.http.get<E>(url)
      .pipe(catchError(() => of(undefined)));
  }

  // Correction

  correctSolution<S, R>(toolId: string, collId: number, exId: number, part: string, solution: S): Observable<R | undefined> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exercises/${exId}/${part}`;

    return this.http.put<R>(url, JSON.stringify(solution), ApiService.putHttpOptions)
      .pipe(catchError(() => of(undefined)));
  }

  // Admin

  adminReadCollections(toolId: string): Observable<ExerciseCollection[]> {
    const url = `${this.adminBaseUrl}/${toolId}/readCollections`;

    return this.http.get<ExerciseCollection[]>(url)
      .pipe(catchError(() => of([])));
  }

  adminReadExercises(toolId: string, collId: number): Observable<Exercise[]> {
    const url = `${this.adminBaseUrl}/${toolId}/collections/${collId}/readExercises`;

    return this.http.get<Exercise[]>(url)
      .pipe(catchError(() => of([])));
  }

  adminUpsertCollection(collection: ExerciseCollection): Observable<boolean> {
    const url = `${this.adminBaseUrl}/${collection.toolId}/collections/${collection.id}`;

    return this.http.put<boolean>(url, collection)
      .pipe(catchError(() => of(false)));
  }

  adminUpsertExercise(toolId: string, collId: number, exercise: Exercise): Observable<boolean> {
    const url = `${this.adminBaseUrl}/${toolId}/collections/${collId}/exercises/${exercise.id}`;

    return this.http.put<boolean>(url, exercise)
      .pipe(catchError(() => of(false)));
  }
}
