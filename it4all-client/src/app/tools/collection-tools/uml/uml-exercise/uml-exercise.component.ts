import {Component, Input, OnInit} from '@angular/core';
import {IExercise, IUmlExerciseContent} from '../../../../_interfaces/models';
import {CollectionTool, ToolPart} from '../../../../_interfaces/tool';
import {UmlTool} from '../uml-tools';

// import * as $ from 'jquery';
// import * as joint from 'jointjs';

@Component({
  selector: 'it4all-uml-exercise',
  templateUrl: './uml-exercise.component.html'
})
export class UmlExerciseComponent implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  exerciseContent: IUmlExerciseContent;

  // model: joint.dia.Graph = new joint.dia.Graph();
  // paper: joint.dia.Paper;

  constructor() {
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as IUmlExerciseContent;

    // this.paper = new joint.dia.Paper({
    //   el: $('#myPaper')
    // });
  }

}
