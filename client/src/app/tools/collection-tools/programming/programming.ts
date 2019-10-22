import {Exercise} from '../../../_interfaces/tool';
import {ExerciseFile} from '../../basics';

interface ProgInputType {
  id: number;
  inputName: string;
  inputType: string;
}

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

// tslint:disable-next-line:no-empty-interface
export interface ProgrammingImplementationPart {

}

// tslint:disable-next-line:no-empty-interface
export interface ProgrammingSampleSolution {

}

export interface ProgrammingExercise extends Exercise {
  functionName: string;
  foldername: string;
  filename: string;

  inputTypes: ProgInputType[];
  outputType: string;

  unitTestPart: ProgrammingUnitTestPart;
  implementationPart: ProgrammingImplementationPart;

  sampleSolutions: ProgrammingSampleSolution[];
}

export function isProgrammingExercise(exercise: Exercise): exercise is ProgrammingExercise {
  return 'unitTestPart' in exercise;
}
