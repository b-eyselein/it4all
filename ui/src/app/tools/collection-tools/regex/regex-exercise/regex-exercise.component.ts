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
import {HasSampleSolutions} from "../../_helpers/correction-helpers";


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
  extends ComponentWithExerciseDirective<string, RegexCorrectionMutation, RegexCorrectionMutationVariables>
  implements OnInit, HasSampleSolutions<string> {

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

  // Sample solutions

  displaySampleSolutions = false;

  toggleSampleSolutions() {
    this.displaySampleSolutions = !this.displaySampleSolutions;
  }

  get sampleSolutions(): string[] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
  }

  // Correction

  protected getSolution(): string {
    return this.solution;
  }

  protected getMutationQueryVariables(): RegexCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part: RegexExPart.RegexSingleExPart,
      userJwt: this.authenticationService.currentUserValue.jwt
    };
  }

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

    this.correctAbstract(this.exerciseFragment, this.partId);
  }

  // Results

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

  // Other

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
