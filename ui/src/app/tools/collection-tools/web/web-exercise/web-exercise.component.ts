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

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent
  extends ComponentWithExercise<WebSolution, WebSolutionInput, WebCorrectionMutation, WebExPart, WebCorrectionGQL, any>
  implements OnInit {

  @Input() part: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: WebExerciseContentSolveFieldsFragment;


  exerciseFiles: ExerciseFile[] = [];

  constructor(webCorrectionGQL: WebCorrectionGQL, dexieService: DexieService) {
    super(webCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.exerciseFiles = this.contentFragment.files;

    this.dexieService.getSolution<ExerciseFile[]>(
      this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part.id
    ).then((oldSolution: DbSolution<ExerciseFile[]> | undefined) => this.exerciseFiles = oldSolution ? oldSolution.solution : []);
  }

  correct(): void {
    const exPart = this.part.id === 'html' ? WebExPart.HtmlPart : WebExPart.JsPart;

    this.correctAbstract(this.exerciseFragment, exPart, this.part);
  }

  protected getSolution(): WebSolution {
    console.info(JSON.stringify(this.exerciseFiles, null, 2));

    return {files: this.exerciseFiles};
  }

  get sampleSolutions(): WebSolution[] {
    return this.contentFragment.webSampleSolution.map((s) => s.webSampleSolutions);
  }

}
