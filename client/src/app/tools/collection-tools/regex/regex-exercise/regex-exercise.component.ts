import {Component, HostListener, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../../../_services/api.service';
import {RegexCorrectionResult, RegexExercise} from '../regex-exercise';
import {RegexTool} from '../../collection-tools-list';
import {Tool} from '../../../../_interfaces/tool';
import {DexieService} from '../../../../_services/dexie.service';

@Component({templateUrl: './regex-exercise.component.html'})
export class RegexExerciseComponent implements OnInit {

  tool: Tool = RegexTool;
  solution = '';

  collId: number;
  exId: number;

  exercise: RegexExercise;

  corrected = false;
  displaySampleSolutions = false;
  result: RegexCorrectionResult;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private dexieService: DexieService) {
    this.collId = parseInt(route.snapshot.paramMap.get('collId'), 10);
    this.exId = parseInt(route.snapshot.paramMap.get('exId'), 10);
  }

  ngOnInit(): void {
    this.apiService.getExercise<RegexExercise>(this.tool.id, this.collId, this.exId)
      .subscribe((exercise: RegexExercise) => this.exercise = exercise);

    this.dexieService.regexSolutions.get([this.collId, this.exId])
      .then((oldSolution) => this.solution = oldSolution ? oldSolution.solution : '');
  }

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

    this.dexieService.regexSolutions.put({
      collId: this.collId, exId: this.exercise.id, solution: this.solution
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

  showSampleSolutions(): void {
    this.displaySampleSolutions = true;
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
