import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {IWebCompleteResult} from '../web-interfaces';
import {
  ExerciseFile,
  ExerciseSolveFieldsFragment,
  WebExerciseContentSolveFieldsFragment
} from "../../../../_services/apollo_services";

import 'codemirror/mode/htmlmixed/htmlmixed';

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent extends ComponentWithExercise<ExerciseFile[], IWebCompleteResult> implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() webExerciseContent: WebExerciseContentSolveFieldsFragment;
  @Input() part: ToolPart;

  exerciseFiles: ExerciseFile[] = [];

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit(): void {
    this.exerciseFiles = this.webExerciseContent.files;

    this.dexieService.getSolution<ExerciseFile[]>(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part.id)
      .then((oldSolution: DbSolution<ExerciseFile[]> | undefined) => this.exerciseFiles = oldSolution ? oldSolution.solution : []);
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part);
  }

  protected getSolution(): ExerciseFile[] {
    return this.exerciseFiles;
  }

  get sampleSolutions(): ExerciseFile[][] {
    return this.webExerciseContent.webSampleSolutions.map((s) => s.sample);
  }

}
