import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ComponentWithCollectionTool} from '../../_helpers/ComponentWithCollectionTool';
import {IExercise, IUmlExerciseContent} from '../../../../_interfaces/models';
import {ToolPart} from '../../../../_interfaces/tool';

// import * as $ from 'jquery';
// import * as joint from 'jointjs';

@Component({
  selector: 'it4all-uml-exercise',
  templateUrl: './uml-exercise.component.html'
})
export class UmlExerciseComponent extends ComponentWithCollectionTool implements OnInit {

  @Input() exercise: IExercise;
  @Input() part: ToolPart;

  exerciseContent: IUmlExerciseContent;

  // model: joint.dia.Graph = new joint.dia.Graph();
  // paper: joint.dia.Paper;

  constructor(private route: ActivatedRoute) {
    super(route);
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as IUmlExerciseContent;

    // this.paper = new joint.dia.Paper({
    //   el: $('#myPaper')
    // });
  }

}
