import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {DexieService} from '../../../../_services/dexie.service';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  WebExerciseContentFragment
} from '../../../../_services/apollo_services';
import {
  WebAbstractResultFragment,
  WebCorrectionGQL,
  WebCorrectionMutation,
  WebCorrectionMutationVariables,
  WebCorrectionResultFragment,
  WebInternalErrorResultFragment,
  WebResultFragment
} from '../web-apollo-mutations.service';
import {FilesSolution, FilesSolutionInput, WebExPart} from '../../../../_interfaces/graphql-types';
import {AuthenticationService} from '../../../../_services/authentication.service';

import 'codemirror/mode/htmlmixed/htmlmixed';
import {FilesExerciseComponent} from "../../_components/files-exercise/files-exercise.component";

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

  constructor(private authenticationService: AuthenticationService, webCorrectionGQL: WebCorrectionGQL, dexieService: DexieService) {
    super(webCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.partId = getIdForWebExPart(this.contentFragment.part);

    this.exerciseFiles = this.contentFragment.files;

    this.loadOldSolutionAbstract(
      this.exerciseFragment,
      this.partId,
      (oldSolution) => this.exerciseFiles = oldSolution.files
    );
  }

  // Sample solutions

  get sampleSolutions(): FilesSolution[] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
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
      part: this.contentFragment.part,
      userJwt: this.authenticationService.currentUserValue.jwt
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

  get abstractResult(): WebAbstractResultFragment & (WebInternalErrorResultFragment | WebResultFragment) | null {
    return this.correctionResult?.result;
  }

  get result(): WebResultFragment | null {
    return this.abstractResult?.__typename === 'WebResult' ? this.abstractResult : null;
  }

}
