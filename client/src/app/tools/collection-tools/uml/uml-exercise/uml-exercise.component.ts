import {Component, OnInit} from '@angular/core';
import * as $ from 'jquery';
import * as joint from 'jointjs';

@Component({
  selector: 'it4all-uml-exercise',
  templateUrl: './uml-exercise.component.html',
})
export class UmlExerciseComponent implements OnInit {

  model: joint.dia.Graph = new joint.dia.Graph();
  paper: joint.dia.Paper;

  constructor() {
  }

  ngOnInit() {
    this.paper = new joint.dia.Paper({
      el: $('#myPaper')
    });
  }

}
