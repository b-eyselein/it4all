import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Exercise, ExerciseCollection} from '../_interfaces/tool';
import {Observable, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class ApiService {

  constructor(private http: HttpClient) {
  }

  static putHttpOptions: { headers: HttpHeaders } = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  private readonly baseUrl = '/api/tools';


  getCollections(toolId: string): Observable<ExerciseCollection[]> {
    return this.http.get<ExerciseCollection[]>(`${this.baseUrl}/${toolId}/collections`);
  }

  getCollection(toolId: string, collId: number): Observable<ExerciseCollection | undefined> {
    return this.http.get<ExerciseCollection>(`${this.baseUrl}/${toolId}/collections/${collId}`)
      .pipe(catchError(() => of(undefined)));
  }

  getExercises<E extends Exercise>(toolId: string, collId: number): Observable<E[]> {
    return this.http.get<E[]>(`${this.baseUrl}/${toolId}/collections/${collId}/exercises`);
  }

  getExercise<E extends Exercise>(toolId: string, collId: number, exId: number): Observable<E> {
    return this.http.get<E>(`${this.baseUrl}/${toolId}/collections/${collId}/exercises/${exId}`)
      .pipe(catchError(() => of(undefined)));
  }

  correctSolution<SolType, ResType>(toolId: string, collId: number, exId: number, part: string, solution: SolType): Observable<ResType> {
    const url = `${this.baseUrl}/${toolId}/collections/${collId}/exercises/${exId}/${part}`;

    return this.http.put<ResType>(url, JSON.stringify(solution), ApiService.putHttpOptions);
  }

}
