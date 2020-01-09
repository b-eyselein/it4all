import {Component, Input, OnInit} from '@angular/core';
import {IExercise, IUmlExerciseContent} from '../../../../_interfaces/models';

import * as joint from 'jointjs';

@Component({
  selector: 'it4all-uml-diagram-drawing',
  templateUrl: './uml-diagram-drawing.component.html',
  styles: [`
    #myPaper {
      border: 1px solid slategrey;
    }
  `]
})
export class UmlDiagramDrawingComponent implements OnInit {

  @Input() exercise: IExercise;

  exerciseContent: IUmlExerciseContent;

  graph = new joint.dia.Graph();
  paper: joint.dia.Paper;

  constructor() {
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as IUmlExerciseContent;

    this.paper = new joint.dia.Paper({
      el: $('#myPaper'),
      model: this.graph,
      width: '100%', height: 700
    });

    this.graph.addCell(
      new joint.shapes.basic.Rect({
        position: {x: 100, y: 100},
        size: {width: 100, height: 100}
      })
    );
  }

}
