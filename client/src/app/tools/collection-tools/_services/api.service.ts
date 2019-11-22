import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Exercise, ExerciseCollection, ExerciseContent} from '../../../_interfaces/exercise';

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

  getExercises<EC extends ExerciseContent>(toolId: string, collId: number): Observable<Exercise<EC>[]> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exercises`;

    // TODO: send current version of known exercises to only get diff!
    return this.http.get<Exercise<EC>[]>(url)
      .pipe(catchError(() => of([])));
  }

  getExercise<EC extends ExerciseContent>(toolId: string, collId: number, exId: number): Observable<Exercise<EC> | undefined> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exercises/${exId}`;

    // TODO: send current version of exercise if known to only get if different!
    return this.http.get<Exercise<EC>>(url)
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

  adminReadExercises<EC extends ExerciseContent>(toolId: string, collId: number): Observable<Exercise<EC>[]> {
    const url = `${this.adminBaseUrl}/${toolId}/collections/${collId}/readExercises`;

    return this.http.get<Exercise<EC>[]>(url)
      .pipe(catchError(() => of([])));
  }

  adminUpsertCollection(collection: ExerciseCollection): Observable<boolean> {
    const url = `${this.adminBaseUrl}/${collection.toolId}/collections/${collection.id}`;

    return this.http.put<boolean>(url, collection)
      .pipe(catchError(() => of(false)));
  }

  adminUpsertExercise(toolId: string, collId: number, exercise: Exercise<any>): Observable<boolean> {
    const url = `${this.adminBaseUrl}/${toolId}/collections/${collId}/exercises/${exercise.id}`;

    return this.http.put<boolean>(url, exercise)
      .pipe(catchError(() => of(false)));
  }
}
