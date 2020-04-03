import {Component, HostListener, Input, OnInit} from '@angular/core';
import {ApiService} from '../../_services/api.service';
import {DexieService} from '../../../../_services/dexie.service';
import {RegexExercisePart} from '../regex-tool';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {
  ExerciseSolveFieldsFragment,
  RegexExerciseContentSolveFieldsFragment
} from '../../../../_services/apollo_services';
import {ToolPart} from '../../../../_interfaces/tool';
import {
  RegexCorrectionGQL,
  RegexCorrectionMutation,
  RegexExtractionResultFragment,
  RegexIllegalRegexResultFragment,
  RegexMatchingResultFragment
} from '../regex-apollo-mutations.service';
import {RegexExPart} from "../../../../_interfaces/graphql-types";


@Component({
  selector: 'it4all-regex-exercise',
  templateUrl: './regex-exercise.component.html'
})
export class RegexExerciseComponent
  extends ComponentWithExercise<string, string, RegexCorrectionMutation, RegexExPart, RegexCorrectionGQL, any>
  implements OnInit {

  @Input() oldPart: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() regexExerciseContent: RegexExerciseContentSolveFieldsFragment;

  solution = '';

  showInfo = false;

  constructor(regexCorrectionGQL: RegexCorrectionGQL, apiService: ApiService, dexieService: DexieService) {
    super(regexCorrectionGQL, apiService, dexieService);
  }

  ngOnInit(): void {
    this.loadOldSolutionAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, RegexExercisePart)
      .then((oldSol) => this.solution = oldSol ? oldSol : '');
  }

  protected getSolution(): string {
    return this.solution;
  }

  get sampleSolutions(): string[] {
    return this.regexExerciseContent.regexSampleSolutions.map((s) => s.sample);
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

    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, RegexExPart.RegexSingleExPart, this.oldPart);
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
