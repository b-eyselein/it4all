import {IExerciseCollection, IExerciseMetaData} from './models';

export interface ExerciseContent {
  tags?: string[];
}

export interface IExerciseCollectionWithExerciseMetaData extends IExerciseCollection {
  exercises?: IExerciseMetaData[];
}

// Solutions

export interface DbSolution<T> {
  toolId: string;
  collId: number;
  exId: number;
  partId: string;
  solution: T;
}
