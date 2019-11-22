import {Component, OnInit} from '@angular/core';
import {UmlExerciseContent} from '../uml-interfaces';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {ExerciseComponentHelpers} from '../../_helpers/ExerciseComponentHelpers';
import {Exercise, ExerciseCollection} from '../../../../_interfaces/exercise';

import * as $ from 'jquery';
import * as joint from 'jointjs';


@Component({templateUrl: './uml-exercise.component.html'})
export class UmlExerciseComponent extends ExerciseComponentHelpers<UmlExerciseContent> implements OnInit {

  collection: ExerciseCollection;
  exercise: Exercise<UmlExerciseContent>;

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
