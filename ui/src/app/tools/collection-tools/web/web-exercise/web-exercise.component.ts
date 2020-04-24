import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {
  ExerciseSolveFieldsFragment,
  WebExerciseContentSolveFieldsFragment
} from '../../../../_services/apollo_services';
import {WebCorrectionGQL, WebCorrectionMutation} from '../web-apollo-mutations.service';
import {ExerciseFile, WebExPart, WebSolution, WebSolutionInput} from '../../../../_interfaces/graphql-types';

import 'codemirror/mode/htmlmixed/htmlmixed';
import {HtmlPart, JsPart} from "../web-tool";

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent
  extends ComponentWithExercise<WebSolution, WebSolutionInput, WebCorrectionMutation, WebExPart, WebCorrectionGQL>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: WebExerciseContentSolveFieldsFragment;

  part: ToolPart;

  exerciseFiles: ExerciseFile[] = [];

  constructor(webCorrectionGQL: WebCorrectionGQL, dexieService: DexieService) {
    super(webCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    switch (this.contentFragment.part) {
      case WebExPart.HtmlPart:
        this.part = HtmlPart;
        break;
      case WebExPart.JsPart:
        this.part = JsPart;
    }

    this.exerciseFiles = this.contentFragment.files;

    this.dexieService
      .getSolution<ExerciseFile[]>(this.exerciseFragment, this.part.id)
      .then((oldSolution: DbSolution<ExerciseFile[]> | undefined) => this.exerciseFiles = oldSolution ? oldSolution.solution : []);
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.contentFragment.part, this.part.id);
  }

  protected getSolution(): WebSolution {
    console.info(JSON.stringify(this.exerciseFiles, null, 2));

    return {files: this.exerciseFiles};
  }

  get sampleSolutions(): WebSolution[] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
  }

}
