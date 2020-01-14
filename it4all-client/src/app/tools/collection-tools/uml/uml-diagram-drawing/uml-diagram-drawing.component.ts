import {Component, Input, OnInit} from '@angular/core';
import {IExercise} from '../../../../_interfaces/models';
import {MyJointClass, STD_CLASS_HEIGHT, STD_CLASS_WIDTH} from '../_model/joint-class-diag-elements';
import {getUmlExerciseTextParts, SelectableClass, UmlExerciseTextPart} from '../uml-tools';
import {GRID_SIZE, PAPER_HEIGHT} from '../_model/uml-consts';
import {findFreePositionForNextClass} from '../_model/class-diag-helpers';

import * as joint from 'jointjs';
import {IUmlExerciseContent} from '../uml-interfaces';

enum CreatableClassDiagramObject {
  Class,
  Association,
  Implementation
}

interface SelectableClassDiagramObject {
  name: string;
  key: CreatableClassDiagramObject;
  selected: boolean;
}

@Component({
  selector: 'it4all-uml-diagram-drawing',
  templateUrl: './uml-diagram-drawing.component.html',
  styles: [`
    #myPaper {
      border: 1px solid slategrey;
      border-radius: 5px;
    }
  `]
})
export class UmlDiagramDrawingComponent implements OnInit {

  @Input() exercise: IExercise;

  exerciseContent: IUmlExerciseContent;

  graph: joint.dia.Graph = new joint.dia.Graph();
  paper: joint.dia.Paper;

  selectableClasses: SelectableClass[];
  umlExerciseTextParts: UmlExerciseTextPart[];

  readonly creatableClassDiagramObjects: SelectableClassDiagramObject[] = [
    {name: 'Klasse', key: CreatableClassDiagramObject.Class, selected: false},
    {name: 'Assoziation', key: CreatableClassDiagramObject.Association, selected: false},
    {name: 'Vererbung', key: CreatableClassDiagramObject.Implementation, selected: false}
  ];

  ngOnInit() {
    this.exerciseContent = this.exercise.content as IUmlExerciseContent;

    const {selectableClasses, textParts} = getUmlExerciseTextParts(this.exercise);

    this.selectableClasses = selectableClasses;
    this.umlExerciseTextParts = textParts;

    const paperJQueryElement = $('#myPaper');

    this.paper = new joint.dia.Paper({
      el: paperJQueryElement, model: this.graph,
      width: Math.floor(paperJQueryElement.width()), height: PAPER_HEIGHT,
      gridSize: GRID_SIZE, drawGrid: {name: 'dot'},
      elementView: joint.dia.ElementView.extend({
        pointerdblclick: (event: joint.dia.Event, x, y) => {
          console.info(event);
        }
      }),
    });

    this.paper.on('blank:pointerclick', (evt: joint.dia.Event, x: number, y: number) => {

        const selectedObjectToCreate: SelectableClassDiagramObject =
          this.creatableClassDiagramObjects.find((ccdo) => ccdo.selected);

        switch (selectedObjectToCreate.key) {
          case CreatableClassDiagramObject.Association:
            break;
          case CreatableClassDiagramObject.Implementation:
            break;
          case CreatableClassDiagramObject.Class:
            this.addClassToGraph({name: 'Klasse 1', selected: false, isCorrect: false});
            break;
        }
      }
    );
  }

  toggle(toCreate: SelectableClassDiagramObject): void {
    this.creatableClassDiagramObjects.forEach((cdo) => cdo.selected = (cdo.key === toCreate.key) ? !cdo.selected : false);
  }

  addClassToGraph(selectableClass: SelectableClass): void {
    if (selectableClass.selected) {
      // Class is already in graph!
      return;
    }

    selectableClass.selected = true;

    this.graph.addCell(
      new MyJointClass({
        className: selectableClass.name,
        size: {width: STD_CLASS_WIDTH, height: STD_CLASS_HEIGHT},
        position: findFreePositionForNextClass(this.paper)
      })
    );
  }

}
