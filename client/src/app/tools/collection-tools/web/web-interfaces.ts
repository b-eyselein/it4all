import {ExerciseFile, IdeWorkspace} from '../../basics';
import {DbSolution, ExerciseContent} from '../../../_interfaces/exercise';

// tslint:disable-next-line:no-empty-interface
export interface WebExerciseContent extends ExerciseContent {

  files: ExerciseFile[];
}

export interface DbWebSolution extends DbSolution<IdeWorkspace> {
}
