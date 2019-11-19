import {Injectable} from '@angular/core';
import Dexie from 'dexie';
import {ExerciseBasics, ExerciseCollection} from '../_interfaces/tool';
import {DbSqlSolution, SqlExercise} from '../tools/collection-tools/sql/sql-exercise';
import {DbRegexSolution, RegexExercise} from '../tools/collection-tools/regex/regex-exercise';
import {DbProgrammingSolution, ProgrammingExercise} from '../tools/collection-tools/programming/programming-interfaces';
import {DbWebSolution, WebExercise} from '../tools/collection-tools/web/web-interfaces';
import {DbUmlSolution, UmlExercise} from '../tools/collection-tools/uml/uml';
import {DbXmlSolution, XmlExercise} from '../tools/collection-tools/xml/xml-interfaces';

@Injectable({providedIn: 'root'})
export class DexieService extends Dexie {

  // tools: Dexie.Table<Tool, string>;
  collections: Dexie.Table<ExerciseCollection, [string, number]>;
  exerciseBasics: Dexie.Table<ExerciseBasics, [string, number, number]>;

  programmingExercises: Dexie.Table<ProgrammingExercise, [number, number]>;
  programmingSolutions: Dexie.Table<DbProgrammingSolution, [number, number]>;

  regexExercises: Dexie.Table<RegexExercise, [number, number]>;
  regexSolutions: Dexie.Table<DbRegexSolution, [number, number]>;

  sqlExercises: Dexie.Table<SqlExercise, [number, number]>;
  sqlSolutions: Dexie.Table<DbSqlSolution, [number, number]>;

  umlExercises: Dexie.Table<UmlExercise, [number, number]>;
  umlSolutions: Dexie.Table<DbUmlSolution, [number, number]>;

  webExercises: Dexie.Table<WebExercise, [number, number]>;
  webSolutions: Dexie.Table<DbWebSolution, [number, number]>;

  xmlExercises: Dexie.Table<XmlExercise, [number, number]>;
  xmlSolutions: Dexie.Table<DbXmlSolution, [number, number]>;

  constructor() {
    super('it4all-client');

    this.version(1).stores({
      // tools: 'id',
      collections: '[toolId+id]',
      exerciseBasics: '[toolId+collId+id]',

      programmingExercises: '[collId+id]',
      programmingSolutions: '[collId+exId]',

      regexExercises: '[collId+id]',
      regexSolutions: '[collId+exId]',

      sqlExercises: '[collId+id]',
      sqlSolutions: '[collId+exId]',

      umlExercises: '[collId+id]',
      umlSolutions: '[collId+exId]',

      webExercises: '[collId+id]',
      webSolutions: '[collId+exId]',

      xmlExercises: '[collId+id]',
      xmlSolutions: '[collId+exId]',
    });

    // this.tools = this.table('tools');
    this.collections = this.table('collections');
    this.exerciseBasics = this.table('exerciseBasics');

    this.programmingExercises = this.table('programmingExercises');
    this.programmingSolutions = this.table('programmingSolutions');

    this.regexExercises = this.table('regexExercises');
    this.regexSolutions = this.table('regexSolutions');

    this.sqlExercises = this.table('sqlExercises');
    this.sqlSolutions = this.table('sqlSolutions');

    this.umlExercises = this.table('umlExercises');
    this.umlSolutions = this.table('umlSolutions');

    this.webExercises = this.table('webExercises');
    this.webSolutions = this.table('webSolutions');

    this.xmlExercises = this.table('xmlExercises');
    this.xmlSolutions = this.table('xmlSolutions');
  }

}
