import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {DexieService} from '../../../../_services/dexie.service';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  WebExerciseContentSolveFieldsFragment
} from '../../../../_services/apollo_services';
import {
  WebCorrectionGQL,
  WebCorrectionMutation,
  WebCorrectionMutationVariables,
  WebResultFragment
} from '../web-apollo-mutations.service';
import {WebExPart, WebSolution, WebSolutionInput} from '../../../../_interfaces/graphql-types';
import {HtmlPart, JsPart} from "../web-tool";

import 'codemirror/mode/htmlmixed/htmlmixed';
import {AuthenticationService} from "../../../../_services/authentication.service";

@Component({
  selector: 'it4all-web-exercise',
  templateUrl: './web-exercise.component.html'
})
export class WebExerciseComponent
  extends ComponentWithExercise<WebSolution, WebSolutionInput, WebCorrectionMutation, WebExPart, WebCorrectionMutationVariables, WebCorrectionGQL>
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

  protected getSolution(): WebSolutionInput {
    console.info(
      this.exerciseFileFragments
        .find((ef) => ef.name === this.contentFragment.siteSpec.fileName)
        .content
    );

    return {files: this.exerciseFileFragments};
  }

  get sampleSolutions(): WebSolution[] {
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

  get result(): WebResultFragment | null {
    return this.resultQuery?.correctWeb.__typename === 'WebResult' ? this.resultQuery.correctWeb : null;
  }

}
