import {Component, Input, OnInit} from '@angular/core';
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

export function getIdForWebExPart(webExPart: WebExPart): string {
  switch (webExPart) {
    case WebExPart.HtmlPart:
      return 'html';
    case WebExPart.JsPart:
      return 'js';
  }
}

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent
  extends ComponentWithExerciseDirective<FilesSolution, FilesSolutionInput, WebCorrectionMutation, WebExPart, WebCorrectionMutationVariables, WebCorrectionGQL>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: WebExerciseContentFragment;

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

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.contentFragment.part, this.partId);
  }

  protected getSolution(): FilesSolutionInput {
    return {files: this.exerciseFiles};
  }

  get sampleSolutions(): FilesSolution[] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
  }

  protected getMutationQueryVariables(part: WebExPart): WebCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part,
      userJwt: this.authenticationService.currentUserValue.jwt
    };
  }

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
