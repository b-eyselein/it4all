import {Component, HostListener, Input, OnInit} from '@angular/core';
import {DexieService} from '../../../../_services/dexie.service';
import {RegexExercisePart} from '../regex-tool';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {
  ExerciseSolveFieldsFragment,
  RegexExerciseContentSolveFieldsFragment
} from '../../../../_services/apollo_services';
import {
  RegexCorrectionGQL,
  RegexCorrectionMutation,
  RegexCorrectionMutationVariables,
  RegexExtractionResultFragment,
  RegexIllegalRegexResultFragment,
  RegexMatchingResultFragment
} from '../regex-apollo-mutations.service';
import {RegexExPart} from '../../../../_interfaces/graphql-types';
import {AuthenticationService} from "../../../../_services/authentication.service";


@Component({
  selector: 'it4all-regex-exercise',
  templateUrl: './regex-exercise.component.html'
})
export class RegexExerciseComponent
  extends ComponentWithExercise<string, string, RegexCorrectionMutation, RegexExPart, RegexCorrectionMutationVariables, RegexCorrectionGQL>
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

  get regexIllegalRegexResult(): RegexIllegalRegexResultFragment | undefined {
    return this.resultQuery?.correctRegex.__typename === 'RegexIllegalRegexResult' ? this.resultQuery.correctRegex : undefined;
  }

  get regexMatchingResult(): RegexMatchingResultFragment | undefined {
    return this.resultQuery?.correctRegex.__typename === 'RegexMatchingResult' ? this.resultQuery.correctRegex : undefined;
  }

  get regexExtractionResult(): RegexExtractionResultFragment | undefined {
    return this.resultQuery?.correctRegex.__typename === 'RegexExtractionResult' ? this.resultQuery.correctRegex : undefined;
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
