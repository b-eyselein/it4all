import {Component, Input, OnInit} from '@angular/core';
import {ProgrammingCorrectionResult} from '../my-programming-interfaces';
import {ApiService} from '../../_services/api.service';
import {DexieService} from '../../../../_services/dexie.service';
import {ProgrammingImplementationToolPart} from '../programming-tool';
import {ToolPart} from '../../../../_interfaces/tool';
import {IExercise} from '../../../../_interfaces/models';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {IExerciseFile, IProgSolution} from '../programming-interfaces';
import {
  ExerciseSolveFieldsFragment,
  ProgExerciseContentSolveFieldsFragment
} from "../../../../_services/apollo_services";

import 'codemirror/mode/python/python';

@Component({
  selector: 'it4all-programming-exercise',
  templateUrl: './programming-exercise.component.html',
  styleUrls: ['./programming-exercise.component.sass']
})
export class ProgrammingExerciseComponent extends ComponentWithExercise<IProgSolution, ProgrammingCorrectionResult> implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: ProgExerciseContentSolveFieldsFragment;

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  exerciseFiles: IExerciseFile[] = [];

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  get sampleSolutionFilesList(): IExerciseFile[][] {
    return this.exercise ? this.exercise.content.sampleSolutions.map((s) => s.sample.files) : [];
  }

  ngOnInit(): void {
    this.exerciseFiles = (this.part === ProgrammingImplementationToolPart) ?
      this.exercise.content.implementationPart.files :
      this.exercise.content.unitTestPart.unitTestFiles;

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

  protected getSolution(): IProgSolution {
    return {
      files: this.exerciseFiles,
      testData: []
    };
  }

  get sampleSolutions(): IProgSolution[] {
    return [];
  }

  correct(): void {
    this.correctAbstract(this.exercise.id, this.exercise.collectionId, this.exercise.toolId, this.part);
  }

}
