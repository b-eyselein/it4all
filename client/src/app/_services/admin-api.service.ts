import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {ExerciseCollection} from '../_interfaces/tool';
import {catchError} from 'rxjs/operators';


@Injectable({providedIn: 'root'})
export class AdminApiService {

  constructor(private http: HttpClient) {
  }

  private readonly baseUrl = '/api/admin/tools';

  // Reading from yaml

  loadCollections(toolId: string): Observable<ExerciseCollection[]> {
    return this.http.get<ExerciseCollection[]>(`${this.baseUrl}/${toolId}/readCollections`)
      .pipe(catchError(() => of([])));
  }
}
