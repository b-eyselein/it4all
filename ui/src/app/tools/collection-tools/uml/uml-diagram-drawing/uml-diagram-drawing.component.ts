import {Component, Input, OnInit} from '@angular/core';
import {isAssociation, isImplementation, isMyJointClass, MyJointClass} from '../_model/joint-class-diag-elements';
import {getIdForUmlExPart, getUmlExerciseTextParts, SelectableClass, UmlExerciseTextPart} from '../uml-tools';
import {GRID_SIZE, PAPER_HEIGHT} from '../_model/uml-consts';
import {addAssociationToGraph, addClassToGraph, addImplementationToGraph} from '../_model/class-diag-helpers';
import {umlAssocfromConnection, umlImplfromConnection} from '../_model/my-uml-interfaces';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {DexieService} from '../../../../_services/dexie.service';
import {environment} from '../../../../../environments/environment';
import {ExerciseSolveFieldsFragment, UmlExerciseContentFragment} from '../../../../_services/apollo_services';
import {
  UmlAbstractResultFragment,
  UmlCorrectionGQL,
  UmlCorrectionMutation,
  UmlCorrectionMutationVariables,
  UmlCorrectionResultFragment,
  UmlInternalErrorResultFragment,
  UmlResultFragment
} from '../uml-apollo-mutations.service';
import {UmlClassDiagramInput, UmlExPart} from '../../../../_interfaces/graphql-types';

import * as joint from 'jointjs';
import {AuthenticationService} from '../../../../_services/authentication.service';


enum CreatableClassDiagramObject {
  Class,
  Association,
  Implementation
}

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
  extends ComponentWithExerciseDirective<UmlClassDiagramInput, UmlCorrectionMutation, UmlCorrectionMutationVariables>
  implements OnInit {

  readonly nextPartId: string = getIdForUmlExPart(UmlExPart.MemberAllocation);

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: UmlExerciseContentFragment;

  partId: string;

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

  constructor(private authenticationService: AuthenticationService, umlCorrectionGQL: UmlCorrectionGQL, dexieService: DexieService) {
    super(umlCorrectionGQL, dexieService);
  }

  ngOnInit(): void {
    this.partId = getIdForUmlExPart(this.contentFragment.part);

    this.withHelp = this.contentFragment.part === UmlExPart.DiagramDrawingHelp;

    this.creatableClassDiagramObjects = [
      {name: 'Klasse', key: CreatableClassDiagramObject.Class, selected: false, disabled: this.withHelp},
      {name: 'Assoziation', key: CreatableClassDiagramObject.Association, selected: false},
      {name: 'Vererbung', key: CreatableClassDiagramObject.Implementation, selected: false}
    ];

    const {selectableClasses, textParts} = getUmlExerciseTextParts(this.exerciseFragment, this.contentFragment);

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
    const declaration = this.contentFragment.sampleSolutions[0].sample;

    this.loadOldSolutionAbstract(this.exerciseFragment, this.partId,
      (oldSol) => this.loadClassDiagram(oldSol),
      () => this.loadClassDiagram(declaration, false)
    );
  }

  loadClassDiagram(cd: UmlClassDiagramInput, loadAttributesAndMethods: boolean = true): void {
    for (const clazz of cd.classes) {
      const attributes = loadAttributesAndMethods ? clazz.attributes : [];
      const methods = loadAttributesAndMethods ? clazz.methods : [];

      addClassToGraph(clazz.name, this.paper, attributes, methods);
    }

    const allCells: MyJointClass[] = this.graph.getCells().filter(isMyJointClass);

    if (loadAttributesAndMethods) {
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
  }

  createPaperEvents(paper: joint.dia.Paper): void {
    paper.on('blank:pointerclick', (evt: joint.dia.Event, x: number, y: number) => {
        const selectedObjectToCreate: SelectableClassDiagramObject =
          this.creatableClassDiagramObjects.find((scdo) => scdo.selected);

        if (selectedObjectToCreate && CreatableClassDiagramObject.Class === selectedObjectToCreate.key) {
          addClassToGraph('Klasse 1', paper, [], [], {x, y});
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
        // console.info(cellView.model);
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
        console.log(cellView.model);
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
      const read: string = pe.target.result as string;

      const loaded: UmlClassDiagramInput = JSON.parse(read);

      for (const clazz of loaded.classes) {
        addClassToGraph(clazz.name, this.paper, clazz.attributes || [], clazz.methods || []);
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

  // Results

  get correctionResult(): UmlCorrectionResultFragment | null {
    return this.resultQuery?.me.umlExercise?.correct;
  }

  get umlAbstractResult(): UmlAbstractResultFragment & (UmlInternalErrorResultFragment | UmlResultFragment) | null {
    return this.correctionResult?.result;
  }

  get result(): UmlResultFragment | null {
    return this.umlAbstractResult?.__typename === 'UmlResult' ? this.umlAbstractResult : null;
  }

  // Correction

  protected getSolution(): UmlClassDiagramInput {
    return {
      classes: this.graph.getCells().filter(isMyJointClass).map((cell) => cell.getAsUmlClass()),
      associations: this.graph.getLinks().filter(isAssociation).map(umlAssocfromConnection),
      implementations: this.graph.getLinks().filter(isImplementation).map(umlImplfromConnection)
    };
  }

  protected getMutationQueryVariables(): UmlCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part: this.contentFragment.part,
      userJwt: this.authenticationService.currentUserValue.jwt
    };
  }

  correct(): void {
    super.correctAbstract(this.exerciseFragment, this.partId);

    this.corrected = true;
  }

}
