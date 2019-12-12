import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {IExercise, IExerciseCollection, IExerciseMetaData} from '../../../_interfaces/models';

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

  getExerciseMetaData(toolId: string, collId: number): Observable<IExerciseMetaData[]> {
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

  adminUpsertExercise(toolId: string, collId: number, exercise: IExercise): Observable<boolean> {
    const url = `${this.adminBaseUrl}/${toolId}/collections/${collId}/exercises/${exercise.id}`;

    return this.http.put<boolean>(url, exercise)
      .pipe(catchError(() => of(false)));
  }
}
