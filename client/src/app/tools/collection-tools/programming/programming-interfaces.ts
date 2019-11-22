import { SuccessType} from '../../../_interfaces/tool';
import {CorrectionResult, ExerciseFile, IdeWorkspace} from '../../basics';
import {DbSolution, ExerciseContent} from '../../../_interfaces/exercise';

// tslint:disable-next-line:no-empty-interface
interface ProgrammingUnitTestTestConfig {

}

export interface ProgrammingUnitTestPart {
  unitTestType: 'Simplified' | 'Normal';
  unitTestTestsDescription: string;
  unitTestFiles: ExerciseFile[];
  unitTestTestConfigs: ProgrammingUnitTestTestConfig[];
  testFileName: string;
  sampleSolFileNames: string[];
}

export interface ProgrammingImplementationPart {
  base: string;
  files: ExerciseFile[];
  implFileName: string;
  sampleSolFileNames: string[];
}

export interface ProgrammingSampleSolution {
  id: number;
  sample: {
    files: ExerciseFile[];
    testData: any[];
  };
}

export interface ProgrammingExerciseContent extends ExerciseContent {
  functionName: string;
  foldername: string;
  filename: string;

  inputTypes: {
    id: number;
    inputName: string;
    inputType: string;
  }[];
  outputType: string;

  unitTestPart: ProgrammingUnitTestPart;
  implementationPart: ProgrammingImplementationPart;

  sampleSolutions: ProgrammingSampleSolution[];
}

// Database

export interface DbProgrammingSolution extends DbSolution<IdeWorkspace> {
}

// Correction

export interface ProgSolution {
  implementation: string;
  testData: TestData[];
  unitTest: ExerciseFile;
}

export interface ProgSingleResult {
  id: number;
  success: SuccessType;
  // correct: boolean
  input: object;
  awaited: string;
  gotten: string;
  stdout: string | null;
}


export interface TestDataResult {
  id: number;
  successType: 'ERROR' | '';
  correct: boolean;
  input: TestDataInput[];
  output: string;
  awaited: string;
  gotten: string;
}

export interface TestDataCreationResult {
  solutionSaved: boolean;
  results: TestDataResult[];
}

export interface TestDataInput {
  id: number;
  input: string;
}

export interface TestData {
  id: number;
  inputs: TestDataInput[];
  output: string;
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


