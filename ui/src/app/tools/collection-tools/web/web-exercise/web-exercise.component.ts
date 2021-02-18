import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  FilesSolution,
  FilesSolutionInput,
  WebCorrectionGQL,
  WebCorrectionMutation,
  WebCorrectionMutationVariables,
  WebCorrectionResultFragment,
  WebExerciseContentFragment,
  WebExPart,
  WebResultFragment
} from '../../../../_services/apollo_services';
import {FilesExerciseComponent} from "../../_components/files-exercise/files-exercise.component";

import 'codemirror/mode/htmlmixed/htmlmixed';
import {DexieService} from "../../../../_services/dexie.service";

export function getIdForWebExPart(webExPart: WebExPart): string {
  switch (webExPart) {
    case WebExPart.HtmlPart:
      return 'html';
    case WebExPart.JsPart:
      return 'js';
    default:
      throw Error('TODO!');
  }
}

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent
  extends ComponentWithExerciseDirective<FilesSolutionInput, WebCorrectionMutation, WebCorrectionMutationVariables>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: WebExerciseContentFragment;

  @ViewChild(FilesExerciseComponent) filesExerciseComponent: FilesExerciseComponent;

  partId: string;

  exerciseFiles: ExerciseFileFragment[] = [];

  constructor(webCorrectionGQL: WebCorrectionGQL, dexieService: DexieService) {
    super(webCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.partId = getIdForWebExPart(this.contentFragment.webPart);

    this.exerciseFiles = this.contentFragment.files;

    this.loadOldSolutionAbstract(
      this.exerciseFragment,
      this.partId,
      (oldSolution) => this.exerciseFiles = oldSolution.files
    );
  }

  // Sample solutions

  get sampleSolutions(): FilesSolution[] {
    return this.contentFragment.webSampleSolutions;
  }

  // Correction

  protected getSolution(): FilesSolutionInput {
    return {files: this.exerciseFiles};
  }

  protected getMutationQueryVariables(): WebCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part: this.contentFragment.webPart,
    };
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.partId, () => {
      if (this.filesExerciseComponent) {
        this.filesExerciseComponent.toggleCorrectionTab();
      }
    });
  };

  // Results

  get correctionResult(): WebCorrectionResultFragment | null {
    return this.resultQuery?.me.webExercise?.correct;
  }

  get result(): WebResultFragment | null {
    return this.correctionResult?.result;
  }

}
