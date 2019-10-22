import {Injectable} from '@angular/core';
import Dexie from 'dexie';
import {Exercise, ExerciseCollection} from '../_interfaces/tool';
import {DbSqlSolution} from '../tools/collection-tools/sql/sql-exercise';
import {DbRegexSolution} from '../tools/collection-tools/regex/regex-exercise';
import {DbProgrammingSolution} from '../tools/collection-tools/programming/programming';

@Injectable({providedIn: 'root'})
export class DexieService extends Dexie {

  // tools: Dexie.Table<Tool, string>;
  collections: Dexie.Table<ExerciseCollection, [number, string]>;
  exerciseBasics: Dexie.Table<Exercise, [number, number, string]>;

  programmingSolutions: Dexie.Table<DbProgrammingSolution, [number, number]>;
  regexSolutions: Dexie.Table<DbRegexSolution, [number, number]>;
  sqlSolutions: Dexie.Table<DbSqlSolution, [number, number]>;

  constructor() {
    super('it4all-client');

    this.version(1).stores({
      // tools: 'id',
      collections: '[id+toolId]',
      exerciseBasics: '[id+collId+toolId]',

      programmingSolutions: '[collId+exId]',
      regexSolutions: '[collId+exId]',
      sqlSolutions: '[collId+exId]'
    });

    // this.tools = this.table('tools');
    this.collections = this.table('collections');
    this.exerciseBasics = this.table('exerciseBasics');

    this.programmingSolutions = this.table('programmingSolutions');
    this.regexSolutions = this.table('regexSolutions');
    this.sqlSolutions = this.table('sqlSolutions');
  }

}
