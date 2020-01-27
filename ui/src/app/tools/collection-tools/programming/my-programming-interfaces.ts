import {SuccessType} from '../../../_interfaces/tool';
import {CorrectionResult} from '../../basics';


export interface ProgSingleResult {
  id: number;
  success: SuccessType;
  // correct: boolean
  input: object;
  awaited: string;
  gotten: string;
  stdout: string | null;
}

export interface NormalExecutionResult {
  success: SuccessType;
  logs: string;
}

export interface UnitTestTestConfig {
  id: number;
  shouldFail: boolean;
  cause: null | string;
  description: string;
}

export interface UnitTestCorrectionResult {
  testConfig: UnitTestTestConfig;
  successful: boolean;
  file: string;
  stdout: string[];
  stderr: string[];
}

export interface ProgrammingCorrectionResult extends CorrectionResult<ProgSingleResult> {
  simplifiedResults: ProgSingleResult[];
  normalResult: NormalExecutionResult;
  unitTestResults: UnitTestCorrectionResult[];
}


