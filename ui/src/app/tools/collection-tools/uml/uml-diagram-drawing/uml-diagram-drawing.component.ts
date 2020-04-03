import {Component, Input, OnInit} from '@angular/core';
import {isAssociation, isImplementation, isMyJointClass, MyJointClass} from '../_model/joint-class-diag-elements';
import {
  getUmlExerciseTextParts,
  SelectableClass,
  UmlDiagramDrawingHelpPart,
  UmlExerciseTextPart,
  UmlMemberAllocationPart
} from '../uml-tools';
import {GRID_SIZE, PAPER_HEIGHT} from '../_model/uml-consts';
import {addAssociationToGraph, addClassToGraph, addImplementationToGraph} from '../_model/class-diag-helpers';
import {ExportedUmlClassDiagram, umlAssocfromConnection, umlImplfromConnection} from '../_model/my-uml-interfaces';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {ToolPart} from '../../../../_interfaces/tool';
import {ApiService} from '../../_services/api.service';
import {DexieService} from '../../../../_services/dexie.service';
import {environment} from '../../../../../environments/environment';
import {
  ExerciseSolveFieldsFragment,
  UmlClassDiagram,
  UmlClassDiagramInput,
  UmlExerciseContentSolveFieldsFragment,
  UmlExPart
} from '../../../../_services/apollo_services';
import {UmlCorrectionGQL, UmlCorrectionMutation} from '../uml-apollo-service';

import * as joint from 'jointjs';


enum CreatableClassDiagramObject {
  Class,
  Association,
  Implementation
}


/**
 * @deprecated
 */
interface SelectableClassDiagramObject {
  name: string;
  key: CreatableClassDiagramObject;
  selected: boolean;
  disabled?: boolean;
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
export class UmlDiagramDrawingComponent
  extends ComponentWithExercise<UmlClassDiagram, UmlClassDiagramInput, UmlCorrectionMutation, UmlExPart, UmlCorrectionGQL, any>
  implements OnInit {

  readonly nextPart = UmlMemberAllocationPart;

  @Input() part: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() exerciseContent: UmlExerciseContentSolveFieldsFragment;

  withHelp: boolean;

  graph: joint.dia.Graph = new joint.dia.Graph();
  paper: joint.dia.Paper;

  selectableClasses: SelectableClass[];
  umlExerciseTextParts: UmlExerciseTextPart[];

  markedClass: MyJointClass | undefined;
  editedClass: MyJointClass | undefined;
  editedAssociation: joint.shapes.uml.Association | undefined;

  creatableClassDiagramObjects: SelectableClassDiagramObject[];

  corrected = false;

  readonly debug = !environment.production;

  constructor(umlCorrectionGQL: UmlCorrectionGQL, apiService: ApiService, dexieService: DexieService) {
    super(umlCorrectionGQL, apiService, dexieService);
  }

  ngOnInit(): void {
    this.withHelp = this.part == UmlDiagramDrawingHelpPart;

    this.creatableClassDiagramObjects = [
      {name: 'Klasse', key: CreatableClassDiagramObject.Class, selected: false, disabled: this.withHelp},
      {name: 'Assoziation', key: CreatableClassDiagramObject.Association, selected: false},
      {name: 'Vererbung', key: CreatableClassDiagramObject.Implementation, selected: false}
    ];

    const {selectableClasses, textParts} = getUmlExerciseTextParts(this.exerciseFragment, this.exerciseContent);

    this.selectableClasses = selectableClasses;
    this.umlExerciseTextParts = textParts;

    const paperJQueryElement = $('#myPaper');

    this.paper = new joint.dia.Paper({
      el: paperJQueryElement, model: this.graph,
      width: Math.floor(paperJQueryElement.width()), height: PAPER_HEIGHT,
      gridSize: GRID_SIZE, drawGrid: {name: 'dot'}
    });

    this.createPaperEvents(this.paper);

    // load classes

    this.loadOldSolutionAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part)
      .then((oldSol) => {
        if (oldSol) {
          this.loadClassDiagram(oldSol);
        } else {
          this.loadClassDiagram(this.exerciseContent.umlSampleSolutions[0].sample as ExportedUmlClassDiagram);
        }
      });
  }

  loadClassDiagram(cd: ExportedUmlClassDiagram): void {
    for (const clazz of cd.classes) {
      addClassToGraph(clazz.name, this.paper, clazz.position, clazz.attributes, clazz.methods);
    }

    const allCells: MyJointClass[] = this.graph.getCells().filter(isMyJointClass);

    for (const assoc of cd.associations) {
      addAssociationToGraph(
        allCells.find((c) => c.getClassName() === assoc.firstEnd),
        assoc.firstMult === 'UNBOUND' ? '*' : '1',
        allCells.find((c) => c.getClassName() === assoc.secondEnd),
        assoc.secondMult === 'UNBOUND' ? '*' : '1',
        this.graph
      );
    }

    for (const impl of cd.implementations) {
      addImplementationToGraph(
        allCells.find((c) => c.getClassName() === impl.subClass),
        allCells.find((c) => c.getClassName() === impl.superClass),
        this.graph
      );
    }
  }

  createPaperEvents(paper: joint.dia.Paper): void {
    paper.on('blank:pointerclick', (evt: joint.dia.Event, x: number, y: number) => {
        const selectedObjectToCreate: SelectableClassDiagramObject =
          this.creatableClassDiagramObjects.find((scdo) => scdo.selected);

        if (selectedObjectToCreate && CreatableClassDiagramObject.Class === selectedObjectToCreate.key) {
          addClassToGraph('Klasse 1', paper, {x, y});
        }
      }
    );

    paper.on('cell:pointerclick', (cellView: joint.dia.CellView, event: joint.dia.Event /*, x: number, y: number*/) => {
      if (cellView.model instanceof MyJointClass) {
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
      } else {
        console.info(cellView.model);
        event.preventDefault();
        event.stopImmediatePropagation();
        event.stopPropagation();
      }
    });

    paper.on('cell:contextmenu', (cellView: joint.dia.CellView/*, event: joint.dia.Event, x: number, y: number*/) => {
      if (this.withHelp) {
        if (cellView.model instanceof MyJointClass) {
          // Cannot change classes
          return;
        } else if (cellView.model instanceof joint.shapes.uml.Association) {
          // Change association...
          this.editedAssociation = cellView.model;
        }
      } else if (cellView.model instanceof MyJointClass) {
        if (!this.editedClass) {
          this.editedClass = cellView.model as MyJointClass;
        } else {
          this.editedClass = undefined;
        }
      } else {
        console.info(cellView.model);
      }
    });
  }

  createClass(selectableClass: SelectableClass): void {
    if (this.withHelp || selectableClass.selected) {
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
      const read: string = pe.target['result'] as string;

      const loaded: ExportedUmlClassDiagram = JSON.parse(read);

      for (const clazz of loaded.classes) {
        addClassToGraph(clazz.name, this.paper, clazz.position, clazz.attributes || []);
      }

      const allCells = this.graph.getCells().filter(isMyJointClass);

      for (const impl of loaded.implementations) {
        addImplementationToGraph(
          allCells.find((c) => c.getClassName() === impl.subClass),
          allCells.find((c) => c.getClassName() === impl.superClass),
          this.graph
        );
      }
    });

    fileReader.readAsText(files.item(0));
  }

  protected getSolution(): ExportedUmlClassDiagram {
    return {
      classes: this.graph.getCells().filter(isMyJointClass).map((cell) => cell.getAsUmlClass()),
      associations: this.graph.getLinks().filter(isAssociation).map(umlAssocfromConnection),
      implementations: this.graph.getLinks().filter(isImplementation).map(umlImplfromConnection)
    };
  }

  get sampleSolutions(): UmlClassDiagram[] {
    return this.exerciseContent.umlSampleSolutions.map((sample) => sample.sample);
  }

  correct(): void {
    super.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, null, this.part);
    this.corrected = true;
  }

}
