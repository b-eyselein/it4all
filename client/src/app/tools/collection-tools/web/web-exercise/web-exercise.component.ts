import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {WebExerciseContent} from '../web-interfaces';

import 'codemirror/mode/htmlmixed/htmlmixed';
import {ExerciseComponentHelpers} from '../../_helpers/ExerciseComponentHelpers';
import {Exercise} from '../../../../_interfaces/exercise';

@Component({templateUrl: './web-exercise.component.html'})
export class WebExerciseComponent extends ExerciseComponentHelpers<WebExerciseContent> implements OnInit {

  collId: number;
  exId: number;

  exercise: Exercise<WebExerciseContent>;

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    super(route);

    this.collId = parseInt(route.snapshot.paramMap.get('collId'), 10);
    this.exId = parseInt(route.snapshot.paramMap.get('exId'), 10);
  }

  ngOnInit(): void {
    this.apiService.getExercise<WebExerciseContent>(this.tool.id, this.collId, this.exId)
      .subscribe((ex: Exercise<WebExerciseContent>) => this.exercise = ex);
  }

  correct(): void {
    console.error('TODO: correct!');
  }

  showSampleSolution(): void {
    console.error('TODO: show sample solution...');
  }

}
