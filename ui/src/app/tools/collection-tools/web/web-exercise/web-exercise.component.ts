import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {IWebCompleteResult} from '../web-interfaces';
import {
  ExerciseSolveFieldsFragment,
  WebExerciseContentSolveFieldsFragment,
} from '../../../../_services/apollo_services';
import {WebCorrectionGQL, WebCorrectionMutation} from '../../../../_services/apollo-mutation.service';

import 'codemirror/mode/htmlmixed/htmlmixed';
import {ExerciseFile, WebExPart, WebSolution, WebSolutionInput} from "../../../../_interfaces/graphql-types";

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent
  extends ComponentWithExercise<WebSolution, WebSolutionInput, WebCorrectionMutation, WebExPart, WebCorrectionGQL, IWebCompleteResult>
  implements OnInit {


  @Input() part: ToolPart;
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
    const exPart = this.part.id === 'html' ? WebExPart.HtmlPart : WebExPart.JsPart;

    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, exPart, this.part);
  }

  protected getSolution(): WebSolution {
    console.info(JSON.stringify(this.exerciseFiles, null, 2));

    return {files: this.exerciseFiles};
  }

  get sampleSolutions(): WebSolution[] {
    return this.webExerciseContent.webSampleSolutions.map((s) => s.sample);
  }

}
