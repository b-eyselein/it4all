import {Component, HostListener, Input, OnInit} from '@angular/core';
import {DexieService} from '../../../../_services/dexie.service';
import {RegexExercisePart} from '../regex-tool';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {ExerciseSolveFieldsFragment, RegexExerciseContentSolveFieldsFragment} from '../../../../_services/apollo_services';
import {
  RegexAbstractResultFragment,
  RegexCorrectionGQL,
  RegexCorrectionMutation,
  RegexCorrectionMutationVariables,
  RegexExtractionResultFragment,
  RegexInternalErrorResultFragment,
  RegexMatchingResultFragment
} from '../regex-apollo-mutations.service';
import {RegexExPart} from '../../../../_interfaces/graphql-types';
import {AuthenticationService} from '../../../../_services/authentication.service';

@Component({
  selector: 'it4all-regex-exercise',
  templateUrl: './regex-exercise.component.html'
})
export class RegexExerciseComponent
  extends ComponentWithExerciseDirective<string, string, RegexCorrectionMutation, RegexExPart, RegexCorrectionMutationVariables, RegexCorrectionGQL>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: RegexExerciseContentSolveFieldsFragment;

  solution = '';

  showInfo = false;

  constructor(private authenticationService: AuthenticationService, regexCorrectionGQL: RegexCorrectionGQL, dexieService: DexieService) {
    super(regexCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.loadOldSolutionAbstract(this.exerciseFragment, RegexExercisePart.id, (oldSol) => this.solution = oldSol);
  }

  protected getSolution(): string {
    return this.solution;
  }

  get sampleSolutions(): string[] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
  }

  protected getMutationQueryVariables(part: RegexExPart): RegexCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part,
      userJwt: this.authenticationService.currentUserValue.jwt
    };
  }

  get basicResult(): RegexAbstractResultFragment & (RegexInternalErrorResultFragment | RegexMatchingResultFragment | RegexExtractionResultFragment) {
    return this.resultQuery?.me.regexExercise?.correct;
  }

  get regexInternalErrorResult(): RegexInternalErrorResultFragment | null {
    return this.basicResult?.__typename === 'RegexInternalErrorResult' ? this.basicResult : null;
  }

  get regexMatchingResult(): RegexMatchingResultFragment | null {
    return this.basicResult?.__typename === 'RegexMatchingResult' ? this.basicResult : null;
  }

  get regexExtractionResult(): RegexExtractionResultFragment | undefined {
    return this.basicResult?.__typename === 'RegexExtractionResult' ? this.basicResult : undefined;
  }

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

    this.correctAbstract(this.exerciseFragment, RegexExPart.RegexSingleExPart, RegexExercisePart.id);
  }

  // FIXME: make directive?
  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      // if (this.correct) {
      // this.update();
      // } else {
      this.correct();
      // }
    }
  }

}
