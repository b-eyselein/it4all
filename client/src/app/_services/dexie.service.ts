import {Injectable} from '@angular/core';
import Dexie from 'dexie';
import {DbSqlSolution} from '../tools/collection-tools/sql/sql-interfaces';
import {DbRegexSolution} from '../tools/collection-tools/regex/regex-interfaces';
import {DbProgrammingSolution} from '../tools/collection-tools/programming/programming-interfaces';
import {DbWebSolution} from '../tools/collection-tools/web/web-interfaces';
import {DbUmlSolution} from '../tools/collection-tools/uml/uml-interfaces';
import {DbXmlSolution} from '../tools/collection-tools/xml/xml-interfaces';
import {DbSolution} from '../_interfaces/exercise';
import {IExercise, IExerciseCollection} from '../_interfaces/models';

@Injectable({providedIn: 'root'})
export class DexieService extends Dexie {

  collections: Dexie.Table<IExerciseCollection, [string, number]>;
  exercises: Dexie.Table<IExercise, [string, number, number]>;
  solutions: Dexie.Table<DbSolution<any>, []>;

  programmingSolutions: Dexie.Table<DbProgrammingSolution, [number, number]>;

  regexSolutions: Dexie.Table<DbRegexSolution, [number, number]>;

  sqlSolutions: Dexie.Table<DbSqlSolution, [number, number]>;

  umlSolutions: Dexie.Table<DbUmlSolution, [number, number]>;

  webSolutions: Dexie.Table<DbWebSolution, [number, number]>;

  xmlSolutions: Dexie.Table<DbXmlSolution, [number, number]>;

  constructor() {
    super('it4all-client');

    this.version(1).stores({
      collections: '[toolId+id]',
      exercises: '[toolId+collId+id+semanticVersion]',
      solutions: '[toolId+collId+exId+semanticVersion+id]',

      programmingSolutions: '[collId+exId]',

      regexSolutions: '[collId+exId]',

      sqlSolutions: '[collId+exId]',

      umlSolutions: '[collId+exId]',

      webSolutions: '[collId+exId]',

      xmlSolutions: '[collId+exId]',
    });

    // this.tools = this.table('tools');
    this.collections = this.table('collections');
    this.exercises = this.table('exercises');
    this.solutions = this.table('solutions');

    this.programmingSolutions = this.table('programmingSolutions');

    this.regexSolutions = this.table('regexSolutions');

    this.sqlSolutions = this.table('sqlSolutions');

    this.umlSolutions = this.table('umlSolutions');

    this.webSolutions = this.table('webSolutions');

    this.xmlSolutions = this.table('xmlSolutions');
  }

}
