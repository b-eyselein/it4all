import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {IExerciseFile, IWebCompleteResult} from '../web-interfaces';
import {
  ExerciseSolveFieldsFragment,
  WebExerciseContentSolveFieldsFragment
} from "../../../../_services/apollo_services";

import 'codemirror/mode/htmlmixed/htmlmixed';

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent extends ComponentWithExercise<IExerciseFile[], IWebCompleteResult> implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() webExerciseContent: WebExerciseContentSolveFieldsFragment;
  @Input() part: ToolPart;

  exerciseFiles: IExerciseFile[] = [];

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit(): void {
    this.exerciseFiles = this.webExerciseContent.files;

    this.dexieService.getSolution<IExerciseFile[]>(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part.id)
      .then((oldSolution: DbSolution<IExerciseFile[]> | undefined) => this.exerciseFiles = oldSolution ? oldSolution.solution : []);
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part);
  }

  protected getSolution(): IExerciseFile[] {
    return this.exerciseFiles;
  }

  get sampleSolutions(): IExerciseFile[][] {
    return this.webExerciseContent.webSampleSolutions.map((s) => s.sample);
  }

}
