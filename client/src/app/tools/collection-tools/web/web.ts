import {DbSolution, Exercise} from '../../../_interfaces/tool';
import {ExerciseFile, IdeWorkspace} from '../../basics';

// tslint:disable-next-line:no-empty-interface
export interface WebExercise extends Exercise {

  files: ExerciseFile[];
}

export interface DbWebSolution extends DbSolution<IdeWorkspace> {
}
