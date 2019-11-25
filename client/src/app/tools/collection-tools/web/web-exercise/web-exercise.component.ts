import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../_services/api.service';

import 'codemirror/mode/htmlmixed/htmlmixed';
import {ExerciseComponentHelpers} from '../../_helpers/ExerciseComponentHelpers';
import {IExercise} from '../../../../_interfaces/models';

@Component({templateUrl: './web-exercise.component.html'})
export class WebExerciseComponent extends ExerciseComponentHelpers implements OnInit {

  collId: number;
  exId: number;

  exercise: IExercise;

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    super(route);

    this.collId = parseInt(route.snapshot.paramMap.get('collId'), 10);
    this.exId = parseInt(route.snapshot.paramMap.get('exId'), 10);
  }

  ngOnInit(): void {
    this.apiService.getExercise(this.tool.id, this.collId, this.exId)
      .subscribe((ex: IExercise) => this.exercise = ex);
  }

  correct(): void {
    console.error('TODO: correct!');
  }

  showSampleSolution(): void {
    console.error('TODO: show sample solution...');
  }

}
