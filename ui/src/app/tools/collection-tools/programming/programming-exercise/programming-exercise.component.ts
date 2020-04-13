import {Component, Input, OnInit} from '@angular/core';
import {ProgrammingCorrectionResult} from '../my-programming-interfaces';
import {DexieService} from '../../../../_services/dexie.service';
import {ProgrammingImplementationToolPart, ProgrammingTestCreationPart} from '../programming-tool';
import {ToolPart} from '../../../../_interfaces/tool';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {ExerciseSolveFieldsFragment, ProgExerciseContentSolveFieldsFragment} from '../../../../_services/apollo_services';
import {ProgCorrectionGQL, ProgCorrectionMutation} from '../programming-apollo-mutations.service';
import {ExerciseFile, ProgExPart, ProgSolution, ProgSolutionInput} from '../../../../_interfaces/graphql-types';

import 'codemirror/mode/python/python';

@Component({
  selector: 'it4all-programming-exercise',
  templateUrl: './programming-exercise.component.html',
  styleUrls: ['./programming-exercise.component.sass']
})
export class ProgrammingExerciseComponent
  extends ComponentWithExercise<ProgSolution, ProgSolutionInput, ProgCorrectionMutation, ProgExPart, ProgCorrectionGQL, ProgrammingCorrectionResult>
  implements OnInit {


  @Input() oldPart: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: ProgExerciseContentSolveFieldsFragment;


  exerciseFiles: ExerciseFile[] = [];

  constructor(progCorrectionGQL: ProgCorrectionGQL, dexieService: DexieService) {
    super(progCorrectionGQL, dexieService);
  }

  get sampleSolutionFilesList(): ExerciseFile[][] {
    return this.contentFragment.progSampleSolutions.map((s) => s.sample.files);
  }

  ngOnInit(): void {
    this.exerciseFiles = (this.oldPart === ProgrammingImplementationToolPart) ?
      this.contentFragment.implementationPart.files :
      this.contentFragment.unitTestPart.unitTestFiles;

    this.loadOldSolution();
  }

  loadOldSolution(): void {
    //    const maybeOldSol: Promise<IProgSolution | undefined> = this.loadOldSolutionAbstract(this.exercise, this.part);
    // TODO: deactivated for now...
    // this.dexieService.programmingSolutions.get([this.collection.id, this.exercise.id])
    //   .then((oldSolution: DbProgrammingSolution | undefined) => {
    //     if (oldSolution) {
    //       // FIXME: editor does not update...
    //       this.exerciseFiles = oldSolution.solution.files;
    //     }
    //   });
  }

  protected getSolution(): ProgSolution {
    return {
      files: this.exerciseFiles,
//      testData: []
    };
  }

  get sampleSolutions(): ProgSolution[] {
    return [];
  }

  correct(): void {
    let part: ProgExPart;

    if (this.oldPart === ProgrammingTestCreationPart) {
      part = ProgExPart.TestCreation;
    } else if (this.oldPart === ProgrammingImplementationToolPart) {
      part = ProgExPart.Implementation;
    } else {
      throw new Error('Part not recognized!');
    }

    this.correctAbstract(this.exerciseFragment, part, this.oldPart);
  }

}
