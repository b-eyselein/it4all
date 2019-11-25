import {Component, HostListener, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {RegexCorrectionResult} from '../regex-interfaces';
import {DexieService} from '../../../../_services/dexie.service';
import {ExerciseComponentHelpers} from '../../_helpers/ExerciseComponentHelpers';
import {IExercise, IRegexExerciseContent} from '../../../../_interfaces/models';

@Component({templateUrl: './regex-exercise.component.html'})
export class RegexExerciseComponent extends ExerciseComponentHelpers implements OnInit {

  solution = '';

  exercise: IExercise;
  exerciseContent: IRegexExerciseContent;

  corrected = false;
  result: RegexCorrectionResult;

  displaySampleSolutions = false;
  showInfo = false;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private dexieService: DexieService) {
    super(route);
  }

  ngOnInit(): void {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
    const exId: number = parseInt(this.route.snapshot.paramMap.get('exId'), 10);

    this.apiService.getExercise(this.tool.id, collId, exId)
      .subscribe((exercise: IExercise) => {
        this.exercise = exercise;
        this.exerciseContent = exercise.content as IRegexExerciseContent;
      });

    this.dexieService.regexSolutions.get([collId, exId])
      .then((oldSolution) => this.solution = oldSolution ? oldSolution.solution : '');
  }

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

    this.dexieService.regexSolutions.put({
      toolId: this.tool.id, collId: this.exercise.collectionId, exId: this.exercise.id, partId: 'regex', solution: this.solution
    });

    this.apiService
      .correctSolution<string, RegexCorrectionResult>(this.tool.id, this.exercise.collectionId, this.exercise.id, 'regex', this.solution)
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
