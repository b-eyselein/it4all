import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {DexieService} from '../../../../_services/dexie.service';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  FilesSolution,
  FilesSolutionInput, ImplementationCorrectionResultFragment,
  ProgExPart,
  ProgrammingCorrectionGQL,
  ProgrammingCorrectionMutation,
  ProgrammingCorrectionMutationVariables,
  ProgrammingCorrectionResultFragment,
  ProgrammingExerciseContentFragment,
  ProgrammingResultFragment,
  UnitTestCorrectionResultFragment
} from '../../../../_services/apollo_services';
import {FilesExerciseComponent} from "../../_components/files-exercise/files-exercise.component";

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
  extends ComponentWithExerciseDirective<FilesSolutionInput, ProgrammingCorrectionMutation, ProgrammingCorrectionMutationVariables>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: ProgrammingExerciseContentFragment;

  @ViewChild(FilesExerciseComponent) filesExerciseComponent: FilesExerciseComponent;

  partId: string;

  exerciseFiles: ExerciseFileFragment[] = [];

  constructor(programmingCorrectionGQL: ProgrammingCorrectionGQL, dexieService: DexieService) {
    super(programmingCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.partId = getIdForProgExPart(this.contentFragment.programmingPart);

    this.exerciseFiles = (this.contentFragment.programmingPart === ProgExPart.Implementation)
      ? this.contentFragment.implementationPart.files
      : this.contentFragment.unitTestPart.unitTestFiles;

    this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSol: FilesSolutionInput) => this.exerciseFiles = oldSol.files);
  }

  // Sample solutions

  get sampleSolutions(): FilesSolution[] {
    return this.contentFragment.programmingSampleSolutions;
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
      part: this.contentFragment.programmingPart,
    };
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.partId, () => {
      if (this.filesExerciseComponent) {
        this.filesExerciseComponent.toggleCorrectionTab();
      }
    });
  }

  // Results

  get correctionResult(): ProgrammingCorrectionResultFragment | null {
    return this.resultQuery?.me.programmingExercise?.correct;
  }

  get abstractResult(): ProgrammingResultFragment | null {
    return this.correctionResult?.result;
  }

  get unitTestResults(): UnitTestCorrectionResultFragment[] {
    return this.abstractResult.unitTestResults || [];
  }

  get implementationCorrectionResult(): ImplementationCorrectionResultFragment | null {
    return this.abstractResult?.implementationCorrectionResult;
  }

}
