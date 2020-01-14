import {Component, HostListener, Input, OnInit} from '@angular/core';
import {ApiService} from '../../_services/api.service';
import {IRegexCompleteResult, IRegexExerciseContent} from '../regex-interfaces';
import {DexieService} from '../../../../_services/dexie.service';
import {IExercise} from '../../../../_interfaces/models';
import {RegexExercisePart} from '../regex-tool';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';

@Component({
  selector: 'it4all-regex-exercise',
  templateUrl: './regex-exercise.component.html'
})
export class RegexExerciseComponent extends ComponentWithExercise<string, IRegexCompleteResult> implements OnInit {

  @Input() exercise: IExercise;

  exerciseContent: IRegexExerciseContent;

  solution = '';

  corrected = false;

  showInfo = false;

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit(): void {
    this.exerciseContent = this.exercise.content as IRegexExerciseContent;

    this.loadOldSolutionAbstract(this.exercise, RegexExercisePart)
      .then((oldSol) => this.solution = oldSol ? oldSol : '');
  }

  protected getSolution(): string {
    return this.solution;
  }

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

    this.correctAbstract(this.exercise, RegexExercisePart);
  }

  triggerSampleSolutions(): void {
    this.displaySampleSolutions = !this.displaySampleSolutions;
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
