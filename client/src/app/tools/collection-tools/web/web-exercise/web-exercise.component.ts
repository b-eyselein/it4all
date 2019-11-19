import {Component, OnInit} from '@angular/core';
import {Tool} from '../../../../_interfaces/tool';
import {WebTool} from '../web-tool';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {WebExercise} from '../web-interfaces';

import 'codemirror/mode/htmlmixed/htmlmixed';

@Component({templateUrl: './web-exercise.component.html'})
export class WebExerciseComponent implements OnInit {

   tool: Tool = WebTool;
   collId: number;
   exId: number;

   exercise: WebExercise;

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    this.collId = parseInt(route.snapshot.paramMap.get('collId'), 10);
    this.exId = parseInt(route.snapshot.paramMap.get('exId'), 10);
  }

  ngOnInit(): void {
    this.apiService.getExercise<WebExercise>(this.tool.id, this.collId, this.exId)
      .subscribe((ex: WebExercise) => this.exercise = ex);
  }

  correct(): void {
    console.error('TODO: correct!');
  }

  showSampleSolution(): void {
    console.error('TODO: show sample solution...');
  }

}
