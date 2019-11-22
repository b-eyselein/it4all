import {Component, HostListener, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {RegexCorrectionResult, RegexExerciseContent} from '../regex-interfaces';
import {DexieService} from '../../../../_services/dexie.service';
import {ExerciseComponentHelpers} from '../../_helpers/ExerciseComponentHelpers';
import {Exercise} from '../../../../_interfaces/exercise';

@Component({templateUrl: './regex-exercise.component.html'})
export class RegexExerciseComponent extends ExerciseComponentHelpers<RegexExerciseContent> implements OnInit {

  solution = '';

  collId: number;
  exId: number;

  exercise: Exercise<RegexExerciseContent>;

  corrected = false;
  result: RegexCorrectionResult;

  displaySampleSolutions = false;
  showInfo = false;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private dexieService: DexieService) {
    super(route);

    this.collId = parseInt(route.snapshot.paramMap.get('collId'), 10);
    this.exId = parseInt(route.snapshot.paramMap.get('exId'), 10);
  }

  ngOnInit(): void {
    this.apiService.getExercise<RegexExerciseContent>(this.tool.id, this.collId, this.exId)
      .subscribe((exercise: Exercise<RegexExerciseContent>) => this.exercise = exercise);

    this.dexieService.regexSolutions.get([this.collId, this.exId])
      .then((oldSolution) => this.solution = oldSolution ? oldSolution.solution : '');
  }

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

    this.dexieService.regexSolutions.put({
      toolId: this.tool.id, collId: this.collId, exId: this.exercise.id, partId: 'regex', solution: this.solution
    });

    this.apiService
      .correctSolution<string, RegexCorrectionResult>(this.tool.id, this.collId, this.exId, 'regex', this.solution)
      .subscribe((result: RegexCorrectionResult) => {
        this.corrected = true;
        this.result = result;
        // // console.error(JSON.stringify(this.result.extractionResults, null, 2));
        // console.warn(this.result.extractionResults.length);
      });
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
