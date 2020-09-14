import {Component, HostListener, Input, OnInit} from '@angular/core';
import {DexieService} from '../../../../_services/dexie.service';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {ExerciseSolveFieldsFragment, RegexExerciseContentFragment} from '../../../../_services/apollo_services';
import {
  RegexAbstractResultFragment,
  RegexCorrectionGQL,
  RegexCorrectionMutation,
  RegexCorrectionMutationVariables,
  RegexCorrectionResultFragment,
  RegexExtractionResultFragment,
  RegexInternalErrorResultFragment,
  RegexMatchingResultFragment
} from '../regex-apollo-mutations.service';
import {RegexExPart} from '../../../../_interfaces/graphql-types';
import {AuthenticationService} from '../../../../_services/authentication.service';


function getIdForRegexExercisePart(regexExPart: RegexExPart): string {
  switch (regexExPart) {
    case RegexExPart.RegexSingleExPart:
      return 'regex';
  }
}

@Component({
  selector: 'it4all-regex-exercise',
  templateUrl: './regex-exercise.component.html'
})
export class RegexExerciseComponent
  extends ComponentWithExerciseDirective<string, string, RegexCorrectionMutation, RegexExPart, RegexCorrectionMutationVariables, RegexCorrectionGQL>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: RegexExerciseContentFragment;

  solution = '';

  showInfo = false;

  readonly partId: string = getIdForRegexExercisePart(RegexExPart.RegexSingleExPart);

  constructor(private authenticationService: AuthenticationService, regexCorrectionGQL: RegexCorrectionGQL, dexieService: DexieService) {
    super(regexCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSol) => this.solution = oldSol);
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

  get correctionResult(): RegexCorrectionResultFragment | null {
    return this.resultQuery?.me.regexExercise?.correct;
  }

  get abstractResult(): RegexAbstractResultFragment & (RegexInternalErrorResultFragment | RegexMatchingResultFragment | RegexExtractionResultFragment) {
    return this.correctionResult?.result;
  }

  get regexInternalErrorResult(): RegexInternalErrorResultFragment | null {
    return this.abstractResult?.__typename === 'RegexInternalErrorResult' ? this.abstractResult : null;
  }

  get regexMatchingResult(): RegexMatchingResultFragment | null {
    return this.abstractResult?.__typename === 'RegexMatchingResult' ? this.abstractResult : null;
  }

  get regexExtractionResult(): RegexExtractionResultFragment | undefined {
    return this.abstractResult?.__typename === 'RegexExtractionResult' ? this.abstractResult : undefined;
  }

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

    this.correctAbstract(this.exerciseFragment, RegexExPart.RegexSingleExPart, this.partId);
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
