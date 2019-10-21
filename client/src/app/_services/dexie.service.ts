import {Injectable} from '@angular/core';
import Dexie from 'dexie';
import {Exercise, ExerciseCollection} from '../_interfaces/tool';
import {DbSqlSolution} from '../tools/collection-tools/sql/sql-exercise';

@Injectable({
  providedIn: 'root'
})
export class DexieService extends Dexie {

  // tools: Dexie.Table<Tool, string>;
  collections: Dexie.Table<ExerciseCollection, [number, string]>;
  exerciseBasics: Dexie.Table<Exercise, [number, number, string]>;
  sqlSolutions: Dexie.Table<DbSqlSolution, [number, number]>;

  constructor() {
    super('it4all-client');

    this.version(1).stores({
      // tools: 'id',
      collections: '[id+toolId]',
      exerciseBasics: '[id+collId+toolId]',
      sqlSolutions: '[collId+exId]'
    });

    // this.tools = this.table('tools');
    this.collections = this.table('collections');
    this.exerciseBasics = this.table('exerciseBasics');
    this.sqlSolutions = this.table('sqlSolutions');
  }

}