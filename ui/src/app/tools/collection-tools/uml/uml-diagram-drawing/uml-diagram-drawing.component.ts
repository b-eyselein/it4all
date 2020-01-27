import {Component, Input, OnInit} from '@angular/core';
import {IExercise} from '../../../../_interfaces/models';
import {MyJointClass} from '../_model/joint-class-diag-elements';
import {getUmlExerciseTextParts, SelectableClass, UmlExerciseTextPart} from '../uml-tools';
import {GRID_SIZE, PAPER_HEIGHT} from '../_model/uml-consts';
import {IUmlExerciseContent} from '../uml-interfaces';
import {addAssociationToGraph, addClassToGraph, addImplementationToGraph} from '../_model/class-diag-helpers';

import * as joint from 'jointjs';
import {ExportedUmlClassDiagram, UmlClassAttribute} from '../_model/my-uml-interfaces';

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

  readonly visibilities = ['+', '-', '#', '~'];
  readonly umlTypes = ['String', 'int', 'double', 'char', 'boolean', 'void'];

  @Input() exercise: IExercise;

  exerciseContent: IUmlExerciseContent;

  graph: joint.dia.Graph = new joint.dia.Graph();
  paper: joint.dia.Paper;

  selectableClasses: SelectableClass[];
  umlExerciseTextParts: UmlExerciseTextPart[];

  markedClass: MyJointClass | undefined;
  editedClass: MyJointClass | undefined;

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
      gridSize: GRID_SIZE, drawGrid: {name: 'dot'}
    });

    this.createPaperEvents(this.paper);
  }

  createPaperEvents(paper: joint.dia.Paper): void {
    paper.on('blank:pointerclick', (evt: joint.dia.Event, x: number, y: number) => {
        const selectedObjectToCreate: SelectableClassDiagramObject =
          this.creatableClassDiagramObjects.find((scdo) => scdo.selected);

        if (CreatableClassDiagramObject.Class === selectedObjectToCreate.key) {
          addClassToGraph('Klasse 1', paper, {x, y});
        }
      }
    );

    paper.on('cell:pointerclick', (cellView: joint.dia.CellView/*, event: joint.dia.Event, x: number, y: number*/) => {

      const selectedObjectToCreate: SelectableClassDiagramObject =
        this.creatableClassDiagramObjects.find((scdo) => scdo.selected);

      if (!this.markedClass) {
        this.markedClass = cellView.model as MyJointClass;
        cellView.highlight();
      } else if (this.markedClass === cellView.model) {
        this.markedClass = undefined;
        cellView.unhighlight();
      } else if (selectedObjectToCreate) {
        if (selectedObjectToCreate.key === CreatableClassDiagramObject.Association) {
          addAssociationToGraph(this.markedClass, '*', cellView.model as MyJointClass, '*', this.graph);
        } else if (selectedObjectToCreate.key === CreatableClassDiagramObject.Implementation) {
          addImplementationToGraph(this.markedClass, cellView.model as MyJointClass, this.graph);
        }

        this.markedClass.findView(paper).unhighlight();
        this.markedClass = undefined;
      }
    });

    paper.on('cell:pointerdblclick', (cellView: joint.dia.CellView/*, event: joint.dia.Event, x: number, y: number*/) => {
      if (!this.editedClass) {
        this.editedClass = cellView.model as MyJointClass;
      } else {
        this.editedClass = undefined;
      }
    });
  }

  createClass(selectableClass: SelectableClass): void {
    if (selectableClass.selected) {
      // Class is already in graph!
      return;
    }

    selectableClass.selected = true;

    addClassToGraph(selectableClass.name, this.paper);
  }

  toggle(toCreate: SelectableClassDiagramObject): void {
    this.creatableClassDiagramObjects.forEach((scdo) => scdo.selected = (scdo.key === toCreate.key) ? !scdo.selected : false);
  }

  readFile(files: FileList): void {
    const fileReader = new FileReader();

    fileReader.onload = ((pe) => {
      const read: string = pe.target['result'];

      const loaded: ExportedUmlClassDiagram = JSON.parse(read);

      for (const clazz of loaded.classes) {
        addClassToGraph(clazz.name, this.paper, clazz.position, clazz.attributes || []);
      }

      for (const impl of loaded.implementations) {
        const allCells = this.graph.getCells();

        addImplementationToGraph(
          allCells.find((c) => (c instanceof MyJointClass) && c.getClassName() === impl.subClass) as MyJointClass,
          allCells.find((c) => (c instanceof MyJointClass) && c.getClassName() === impl.superClass) as MyJointClass,
          this.graph
        );
      }
    });

    fileReader.readAsText(files.item(0));
  }

  removeAttribute(attr: UmlClassAttribute): void {
    const newAttributes = this.editedClass.getAttributes().filter((a) => a !== attr);
    this.editedClass.setAttributes(newAttributes);

    console.info(this.editedClass.getAttributes());
  }

}
