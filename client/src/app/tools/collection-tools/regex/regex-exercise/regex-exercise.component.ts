import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../../../_services/api.service';
import {RegexCorrectionResult, RegexExercise} from '../regex';
import {RegexTool} from '../../collection-tools-list';
import {Tool} from '../../../../_interfaces/tool';

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

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    this.collId = parseInt(route.snapshot.paramMap.get('collId'), 10);
    this.exId = parseInt(route.snapshot.paramMap.get('exId'), 10);
  }

  ngOnInit(): void {
    this.apiService.getExercise<RegexExercise>(this.tool.id, this.collId, this.exId)
      .subscribe((exercise: RegexExercise) => this.exercise = exercise);
  }

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

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

}
