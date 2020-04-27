import {Component, Input, OnInit} from '@angular/core';
import {DexieService} from '../../../../_services/dexie.service';
import {ProgrammingImplementationToolPart, ProgrammingTestCreationPart} from '../programming-tool';
import {ToolPart} from '../../../../_interfaces/tool';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  ProgExerciseContentSolveFieldsFragment
} from '../../../../_services/apollo_services';
import {
  NormalExecutionResultFragment,
  ProgrammingCorrectionGQL,
  ProgrammingCorrectionMutation,
  ProgrammingResultFragment,
  SimplifiedExecutionResultFragment,
  UnitTestCorrectionResultFragment
} from '../programming-apollo-mutations.service';
import {ProgExPart, ProgSolution, ProgSolutionInput} from '../../../../_interfaces/graphql-types';

import 'codemirror/mode/python/python';

@Component({
  selector: 'it4all-programming-exercise',
  templateUrl: './programming-exercise.component.html',
  styleUrls: ['./programming-exercise.component.sass']
})
export class ProgrammingExerciseComponent
  extends ComponentWithExercise<ProgSolution, ProgSolutionInput, ProgrammingCorrectionMutation, ProgExPart, ProgrammingCorrectionGQL>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: ProgExerciseContentSolveFieldsFragment;

  private oldPart: ToolPart;

  exerciseFiles: ExerciseFileFragment[] = [];

  constructor(programmingCorrectionGQL: ProgrammingCorrectionGQL, dexieService: DexieService) {
    super(programmingCorrectionGQL, dexieService);
  }

  get sampleSolutionFilesList(): ExerciseFileFragment[][] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample.files);
  }

  ngOnInit(): void {
    switch (this.contentFragment.part) {
      case ProgExPart.ActivityDiagram:
      case ProgExPart.Implementation:
        this.oldPart = ProgrammingImplementationToolPart;
        break;
      case ProgExPart.TestCreation:
        this.oldPart = ProgrammingTestCreationPart;
        break;
    }

    this.exerciseFiles = (this.contentFragment.part === ProgExPart.Implementation) ?
      this.contentFragment.implementationPart.files :
      this.contentFragment.unitTestPart.unitTestFiles;

    this.loadOldSolution();
  }

  loadOldSolution(): void {
    this.loadOldSolutionAbstract(this.exerciseFragment, this.oldPart.id, (maybeOldSolution: ProgSolutionInput) => {
      console.info(JSON.stringify(maybeOldSolution));
      // TODO: deactivated for now...
      // this.exerciseFiles = maybeOldSolution.files;
    });
  }

  protected getSolution(): ProgSolutionInput {
    return {files: this.exerciseFiles};
  }

  get sampleSolutions(): ProgSolution[] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
  }

  get result(): ProgrammingResultFragment | null {
    return this.resultQuery.correctProgramming.__typename === 'ProgrammingResult' ? this.resultQuery.correctProgramming : null;
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

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.contentFragment.part, this.oldPart.id);
  }

}
