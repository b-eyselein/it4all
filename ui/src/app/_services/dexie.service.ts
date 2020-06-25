import {Injectable} from '@angular/core';
import Dexie from 'dexie';
import {DbSolution} from '../_interfaces/exercise';
import {ExerciseSolveFieldsFragment} from './apollo_services';

@Injectable({providedIn: 'root'})
export class DexieService extends Dexie {

  private solutions: Dexie.Table<DbSolution<any>, [string, number, number, string]>;

  constructor() {
    super('it4all-client');

    this.version(1).stores({
      solutions: '[toolId+collId+exId+partId]',
    });

    this.solutions = this.table('solutions');
  }

  getSolution<T>(exerciseFragment: ExerciseSolveFieldsFragment, partId: string): Dexie.Promise<DbSolution<T> | undefined> {
    return this.solutions.get([exerciseFragment.toolId, exerciseFragment.collectionId, exerciseFragment.exerciseId, partId]);
  }

  upsertSolution<T>(
    exerciseFragment: ExerciseSolveFieldsFragment,
    partId: string,
    solution: T
  ): Dexie.Promise<[string, number, number, string]> {
    return this.solutions.put({
      exId: exerciseFragment.exerciseId,
      collId: exerciseFragment.collectionId,
      toolId: exerciseFragment.toolId,
      partId,
      solution
    });
  }

}
