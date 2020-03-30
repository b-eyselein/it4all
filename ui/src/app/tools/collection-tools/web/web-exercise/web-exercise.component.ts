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
  WebExerciseContentSolveFieldsFragment,
  WebExPart
} from '../../../../_services/apollo_services';
import {WebCorrectionGQL, WebCorrectionMutation} from '../../../../_services/apollo-mutation.service';

import 'codemirror/mode/htmlmixed/htmlmixed';

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent
  extends ComponentWithExercise<ExerciseFile[], WebCorrectionMutation, WebExPart, WebCorrectionGQL, IWebCompleteResult>
  implements OnInit {


  @Input() part: ToolPart;
  @Input() exPart: WebExPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() webExerciseContent: WebExerciseContentSolveFieldsFragment;


  exerciseFiles: ExerciseFile[] = [];

  constructor(webCorrectionGQL: WebCorrectionGQL, apiService: ApiService, dexieService: DexieService) {
    super(webCorrectionGQL, apiService, dexieService);
  }

  ngOnInit(): void {
    this.exerciseFiles = this.webExerciseContent.files;

    this.dexieService.getSolution<ExerciseFile[]>(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part.id)
      .then((oldSolution: DbSolution<ExerciseFile[]> | undefined) => this.exerciseFiles = oldSolution ? oldSolution.solution : []);
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.exPart, this.part);
  }

  protected getSolution(): ExerciseFile[] {
    return this.exerciseFiles;
  }

  get sampleSolutions(): ExerciseFile[][] {
    return this.webExerciseContent.webSampleSolutions.map((s) => s.sample);
  }

}
