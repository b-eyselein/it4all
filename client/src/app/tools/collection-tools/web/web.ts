import {Exercise} from '../../../_interfaces/tool';
import {ExerciseFile} from '../../basics';

// tslint:disable-next-line:no-empty-interface
export interface WebExercise extends Exercise {

  files: ExerciseFile[];
}
