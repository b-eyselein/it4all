import {Component, HostListener, Input, OnInit} from '@angular/core';
import {ApiService} from '../../_services/api.service';
import {IRegexCompleteResult} from '../regex-interfaces';
import {DexieService} from '../../../../_services/dexie.service';
import {IExercise} from '../../../../_interfaces/models';
import {RegexExercisePart} from '../regex-tool';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {
  ExerciseSolveFieldsFragment,
  RegexExerciseContentSolveFieldsFragment
} from "../../../../_services/apollo_services";
import {ToolPart} from "../../../../_interfaces/tool";

@Component({
  selector: 'it4all-regex-exercise',
  templateUrl: './regex-exercise.component.html'
})
export class RegexExerciseComponent extends ComponentWithExercise<string, IRegexCompleteResult> implements OnInit {

  @Input() part: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() regexExerciseContent: RegexExerciseContentSolveFieldsFragment;

  solution = '';

  showInfo = false;

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
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

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part);
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
