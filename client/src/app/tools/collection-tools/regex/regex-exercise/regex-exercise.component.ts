import {Component, HostListener, Input, OnInit} from '@angular/core';
import {ApiService} from '../../_services/api.service';
import {RegexCorrectionResult} from '../regex-interfaces';
import {DexieService} from '../../../../_services/dexie.service';
import {IExercise, IRegexExerciseContent} from '../../../../_interfaces/models';
import {RegexExercisePart, RegexTool} from '../regex-tool';
import {CollectionTool, ToolPart} from '../../../../_interfaces/tool';

@Component({
  selector: 'it4all-regex-exercise',
  templateUrl: './regex-exercise.component.html'
})
export class RegexExerciseComponent implements OnInit {

  tool: CollectionTool = RegexTool;
  part: ToolPart = RegexExercisePart;

  solution = '';

  @Input() exercise: IExercise;
  exerciseContent: IRegexExerciseContent;

  corrected = false;
  result: RegexCorrectionResult;

  displaySampleSolutions = false;
  showInfo = false;

  constructor(private apiService: ApiService, private dexieService: DexieService) {
  }

  ngOnInit(): void {
    this.exerciseContent = this.exercise.content as IRegexExerciseContent;

    this.dexieService.solutions.get([this.tool.id, this.exercise.collectionId, this.exercise.id, this.part.id])
      .then((oldSolution) => this.solution = oldSolution ? oldSolution.solution : '');
  }

  correct(): void {
    if (this.solution === undefined || this.solution.length === 0) {
      alert('Sie können keine leere Lösung abgeben!');
      return;
    }

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.solutions.put({
      toolId: this.tool.id, collId: this.exercise.collectionId, exId: this.exercise.id, partId: this.part.id, solution: this.solution
    });

    this.apiService.correctSolution<string, RegexCorrectionResult>(this.exercise, 'regex', this.solution)
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
