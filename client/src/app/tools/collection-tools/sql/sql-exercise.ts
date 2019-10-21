import {Exercise} from '../../../_interfaces/tool';
import {CorrectionResult} from '../../basics';

// tslint:disable-next-line:no-empty-interface
export interface SqlExercise extends Exercise {
}

export interface SqlResult extends CorrectionResult<any> {
  success: string;
}

export interface DbSqlSolution {
  // toolId: string;
  collId: number;
  exId: number;
  // partId: string;
  solution: string;
}
