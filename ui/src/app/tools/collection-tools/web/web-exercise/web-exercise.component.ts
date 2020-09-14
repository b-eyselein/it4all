import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {DexieService} from '../../../../_services/dexie.service';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  WebExerciseContentSolveFieldsFragment
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
import {HtmlPart, JsPart} from '../web-tool';
import {AuthenticationService} from '../../../../_services/authentication.service';

import 'codemirror/mode/htmlmixed/htmlmixed';


@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent
  extends ComponentWithExerciseDirective<FilesSolution, FilesSolutionInput, WebCorrectionMutation, WebExPart, WebCorrectionMutationVariables, WebCorrectionGQL>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: WebExerciseContentSolveFieldsFragment;

  part: ToolPart;

  exerciseFileFragments: ExerciseFileFragment[] = [];

  constructor(private authenticationService: AuthenticationService, webCorrectionGQL: WebCorrectionGQL, dexieService: DexieService) {
    super(webCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    switch (this.contentFragment.part) {
      case WebExPart.HtmlPart:
        this.part = HtmlPart;
        break;
      case WebExPart.JsPart:
        this.part = JsPart;
        break;
    }

    this.exerciseFileFragments = this.contentFragment.files;

    this.loadOldSolutionAbstract(
      this.exerciseFragment,
      this.part.id,
      (oldSolution) => this.exerciseFileFragments = oldSolution.files
    );
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.contentFragment.part, this.part.id);
  }

  protected getSolution(): FilesSolutionInput {
    return {files: this.exerciseFileFragments};
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
