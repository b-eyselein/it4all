import {Component, Input, OnInit} from '@angular/core';
import {DexieService} from '../../../../_services/dexie.service';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  NormalUnitTestPartFragment,
  ProgrammingExerciseContentFragment
} from '../../../../_services/apollo_services';
import {
  NormalExecutionResultFragment,
  ProgrammingAbstractResultFragment,
  ProgrammingCorrectionGQL,
  ProgrammingCorrectionMutation,
  ProgrammingCorrectionMutationVariables,
  ProgrammingCorrectionResultFragment,
  ProgrammingInternalErrorResultFragment,
  ProgrammingResultFragment,
  SimplifiedExecutionResultFragment,
  UnitTestCorrectionResultFragment
} from '../programming-apollo-mutations.service';
import {FilesSolution, FilesSolutionInput, ProgExPart} from '../../../../_interfaces/graphql-types';
import {AuthenticationService} from '../../../../_services/authentication.service';

import 'codemirror/mode/python/python';

function getIdForProgExPart(progExPart: ProgExPart): string {
  switch (progExPart) {
    case ProgExPart.ActivityDiagram:
    case ProgExPart.Implementation:
      return 'implementation';
    case ProgExPart.TestCreation:
      return 'testCreation';
  }
}

@Component({
  selector: 'it4all-programming-exercise',
  templateUrl: './programming-exercise.component.html'
})
export class ProgrammingExerciseComponent
  extends ComponentWithExerciseDirective<FilesSolutionInput,
    ProgrammingCorrectionMutation,
    ProgExPart,
    ProgrammingCorrectionMutationVariables,
    ProgrammingCorrectionGQL>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: ProgrammingExerciseContentFragment;

  partId: string;

  exerciseFiles: ExerciseFileFragment[] = [];

  constructor(
    private authenticationService: AuthenticationService,
    programmingCorrectionGQL: ProgrammingCorrectionGQL,
    dexieService: DexieService
  ) {
    super(programmingCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.partId = getIdForProgExPart(this.contentFragment.part);

    this.exerciseFiles = (this.contentFragment.part === ProgExPart.Implementation) ?
      this.contentFragment.implementationPart.files :
      (this.contentFragment.unitTestPart as NormalUnitTestPartFragment).unitTestFiles;

    this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSol: FilesSolutionInput) => this.exerciseFiles = oldSol.files);
  }

  // Sample solutions

  get sampleSolutions(): FilesSolution[] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
  }

  // Correction

  protected getSolution(): FilesSolutionInput {
    return {files: this.exerciseFiles};
  }

  protected getMutationQueryVariables(): ProgrammingCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part: this.contentFragment.part,
      userJwt: this.authenticationService.currentUserValue.jwt
    };
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.partId);
  }

  // Results

  get correctionResult(): ProgrammingCorrectionResultFragment | null {
    return this.resultQuery?.me.programmingExercise?.correct;
  }

  get abstractResult(): ProgrammingAbstractResultFragment & (ProgrammingResultFragment | ProgrammingInternalErrorResultFragment) {
    return this.correctionResult?.result;
  }

  get internalErrorResult(): ProgrammingInternalErrorResultFragment | null {
    return this.abstractResult?.__typename === 'ProgrammingInternalErrorResult' ? this.abstractResult : null;
  }

  private get result(): ProgrammingResultFragment | null {
    return this.abstractResult?.__typename === 'ProgrammingResult' ? this.abstractResult : null;
  }

  get simplifiedResults(): SimplifiedExecutionResultFragment[] {
    return this.result ? this.result.simplifiedResults : [];
  }

  get unitTestResults(): UnitTestCorrectionResultFragment[] {
    return this.result ? this.result.unitTestResults : [];
  }

  get normalResult(): NormalExecutionResultFragment | null {
    return this.result?.normalResult;
  }

}
