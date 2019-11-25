import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {ExerciseComponentHelpers} from '../../_helpers/ExerciseComponentHelpers';

import * as $ from 'jquery';
import * as joint from 'jointjs';
import {IExercise, IExerciseCollection} from '../../../../_interfaces/models';


@Component({templateUrl: './uml-exercise.component.html'})
export class UmlExerciseComponent extends ExerciseComponentHelpers implements OnInit {

  collection: IExerciseCollection;
  exercise: IExercise;

  model: joint.dia.Graph = new joint.dia.Graph();
  paper: joint.dia.Paper;

  constructor(private route: ActivatedRoute, private apiService: ApiService) {
    super(route);
  }

  ngOnInit() {
    this.paper = new joint.dia.Paper({
      el: $('#myPaper')
    });
  }

}
