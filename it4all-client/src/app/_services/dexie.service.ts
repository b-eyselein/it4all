import {Injectable} from '@angular/core';
import Dexie from 'dexie';
import {DbSolution} from '../_interfaces/exercise';
import {IExercise, IExerciseCollection} from '../_interfaces/models';

@Injectable({providedIn: 'root'})
export class DexieService extends Dexie {

  collections: Dexie.Table<IExerciseCollection, [string, number]>;
  exercises: Dexie.Table<IExercise, [string, number, number]>;
  private solutions: Dexie.Table<DbSolution<any>, [string, number, number, string]>;

  constructor() {
    super('it4all-client');

    this.version(1).stores({
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
    this.version(2)
      .stores({
        exercises: '[toolId+collId+id+semanticVersion]',
        solutions: '[toolId+collId+exId+partId]',

        exerciseBasics: null,
        programmingExercises: null,
        programmingSolutions: null,

        regexExercises: null,
        regexSolutions: null,

        sqlExercises: null,
        sqlSolutions: null,

        umlExercises: null,
        umlSolutions: null,

        webExercises: null,
        webSolutions: null,

        xmlExercises: null,
        xmlSolutions: null,
      });

    this.collections = this.table('collections');
    this.exercises = this.table('exercises');
    this.solutions = this.table('solutions');
  }

  getSolution<T>(exercise: IExercise, partId: string): Dexie.Promise<DbSolution<T> | undefined> {
    return this.solutions.get([exercise.toolId, exercise.collectionId, exercise.id, partId]);
  }

  upsertSolution<T>(exercise: IExercise, partId: string, solution: T): Dexie.Promise<[string, number, number, string]> {
    return this.solutions.put({
      exId: exercise.id, collId: exercise.collectionId, toolId: exercise.toolId, partId, solution
    });
  }

}
