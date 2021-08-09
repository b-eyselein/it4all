import React, {useEffect, useMemo, useRef, useState} from 'react';
import * as joint from 'jointjs';
import {
  ExerciseSolveFieldsFragment,
  UmlClassDiagramInput,
  UmlClassInput,
  UmlExerciseContentFragment,
  UmlMultiplicity,
  useUmlCorrectionMutation
} from '../../../graphql';
import {BulmaTabs, Tabs} from '../../../helpers/BulmaTabs';
import {useTranslation} from 'react-i18next';
import {GRID_SIZE, PAPER_HEIGHT} from './model/uml-consts';
import {addAssociationToGraph, addClassToGraph, addImplementationToGraph, findFreePositionForNextClass} from './model/class-diag-helpers';
import {isAssociation, isImplementation, isMyJointClass, MyJointClass} from './model/joint-class-diag-elements';
import {UmlDiagramDrawingExerciseTextTabContent} from './UmlDiagramDrawingExerciseTextTabContent';
import {umlAssocFromConnection, umlImplFromConnection} from './model/my-uml-interfaces';
import {database} from '../../DexieTable';
import {UmlDiagramDrawingCorrectionTabContent} from './UmlDiagramDrawingCorrectionTabContent';
import {WithQuery} from '../../../WithQuery';
import {UmlClassEdit} from './UmlClassEdit';

enum CreatableClassDiagramObject {Class, Association, Implementation}

export interface SelectableClassDiagramObject {
  name: string;
  key: CreatableClassDiagramObject;
  disabled?: boolean;
}

export interface UmlDbClass extends UmlClassInput {
  position?: joint.dia.Point;
}

export interface UmlDbClassDiagram extends UmlClassDiagramInput {
  classes: UmlDbClass[];
}

interface IProps {
  exercise: ExerciseSolveFieldsFragment;
  content: UmlExerciseContentFragment;
  withHelp: boolean;
  partId: string;
  oldSolution: UmlDbClassDiagram | undefined;
}

interface IState {
  markedClass?: MyJointClass;
  editedClass?: MyJointClass;
  editedAssociation?: joint.shapes.uml.Association;
  selectedCreatableObject?: SelectableClassDiagramObject;
}

export function UmlDiagramDrawing({exercise, content, withHelp, partId, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const canvas = useRef<HTMLDivElement>(null);

  const [activeTab, setActiveTab] = useState<keyof Tabs>('exerciseText');
  const [state, setState] = useState<IState>({});

  const [correctExercise, correctionMutationResult] = useUmlCorrectionMutation();

  if (!content.umlPart) {
    throw new Error('no part found?');
  }

  const part = content.umlPart;

  // vars get recreated after every rerender!
  const graph: joint.dia.Graph = useMemo(() => new joint.dia.Graph(), []);
  let paper: joint.dia.Paper;

  useEffect(() => {
    if (canvas.current) {
      // Initialize paper
      paper = new joint.dia.Paper({
        el: canvas.current, model: graph,
        width: canvas.current.clientWidth, height: PAPER_HEIGHT,
        gridSize: GRID_SIZE, drawGrid: {name: 'dot'}
      });

      createPaperEvents();

      if (oldSolution) {
        loadClassDiagram(oldSolution, false, true);
      } else if (withHelp) {
        loadClassDiagram(content.umlSampleSolutions[0], true, false);
      }
    }
  }, []);

  const creatableClassDiagramObjects: SelectableClassDiagramObject[] = [
    {name: 'Klasse', key: CreatableClassDiagramObject.Class, disabled: withHelp},
    {name: 'Assoziation', key: CreatableClassDiagramObject.Association},
    {name: 'Vererbung', key: CreatableClassDiagramObject.Implementation}
  ];

  function createPaperEvents(): void {
    paper.on('blank:pointerclick', (evt: joint.dia.Event, x: number, y: number) => setState((state) => {
      if (state.selectedCreatableObject && state.selectedCreatableObject.key === CreatableClassDiagramObject.Class) {
        addClassToGraph(graph, 'Klasse 1', [], [], {x, y});
      }

      return state;
    }));

    paper.on('cell:pointerclick', (cellView) =>
      setState(({markedClass, selectedCreatableObject, ...rest}) => {
        if (!(cellView.model instanceof MyJointClass)) {
          return {...rest, markedClass: undefined, selectedCreatableObject};
        }

        if (!markedClass) {
          // Nothing set yet
          cellView.highlight();
          return {...rest, markedClass: cellView.model, selectedCreatableObject};
        }

        if (markedClass.getClassName() === cellView.model.getClassName()) {
          // class was clicked again
          cellView.unhighlight();
          return {...rest, markedClass: undefined, selectedCreatableObject};
        }

        if (selectedCreatableObject) {
          if (selectedCreatableObject.key === CreatableClassDiagramObject.Association) {
            addAssociationToGraph(graph, markedClass, UmlMultiplicity.Unbound, cellView.model as MyJointClass, UmlMultiplicity.Unbound);
          } else if (selectedCreatableObject.key === CreatableClassDiagramObject.Implementation) {
            addImplementationToGraph(graph, markedClass, cellView.model as MyJointClass);
          }

          markedClass.findView(paper).unhighlight();

          return {...rest, markedClass: undefined, selectedCreatableObject};
        }

        // New class selected, nothing to create
        markedClass.findView(paper).unhighlight();
        cellView.highlight();
        return {...rest, markedClass: cellView.model, selectedCreatableObject};
      })
    );

    paper.on('cell:contextmenu', (cellView) => setState((state) => {
      if (cellView.model instanceof joint.shapes.uml.Association) {
        return {...state, editedAssociation: cellView.model as joint.shapes.uml.Association};
      }

      if (cellView.model instanceof MyJointClass && !withHelp) {
        console.info(state.editedClass);
        return {...state, editedClass: state.editedClass?.getClassName() === cellView.model.getClassName() ? undefined : cellView.model as MyJointClass};
      }

      return state;
    }));
  }

  function loadClassDiagram({classes, associations, implementations}: UmlDbClassDiagram, loadAttrsAndMethods = false, loadAssocsAndImpl = false): void {

    classes.forEach(({name, attributes, methods, position}) => {
      const thePosition = position || findFreePositionForNextClass(paper);
      addClassToGraph(graph, name, loadAttrsAndMethods ? [] : (attributes || []), loadAttrsAndMethods ? [] : (methods || []), thePosition);
    });

    if (loadAssocsAndImpl) {
      const allCells: MyJointClass[] = graph.getCells().filter(isMyJointClass);

      associations.forEach(({firstEnd, firstMult, secondEnd, secondMult}) => {
        const maybeFirstEnd = allCells.find((c) => c.getClassName() === firstEnd);
        const maybeSecondEnd = allCells.find((c) => c.getClassName() === secondEnd);

        if (maybeFirstEnd && maybeSecondEnd) {
          addAssociationToGraph(graph, maybeFirstEnd, firstMult, maybeSecondEnd, secondMult);
        }
      });

      implementations.forEach(({subClass, superClass}) => {
        const maybeSubClass = allCells.find((c) => c.getClassName() === subClass);
        const maybeSuperClass = allCells.find((c) => c.getClassName() === superClass);

        if (maybeSubClass && maybeSuperClass) {
          addImplementationToGraph(graph, maybeSubClass, maybeSuperClass);
        }
      });
    }
  }

  function toggle(x: SelectableClassDiagramObject): void {
    setState((state) => ({...state, selectedCreatableObject: x.key === state.selectedCreatableObject?.key ? undefined : x}));
  }

  function correct(): void {
    const dbSolution: UmlDbClassDiagram = {
      classes: graph.getCells().filter(isMyJointClass).map((cell) => cell.getAsUmlClass()),
      associations: graph.getLinks().filter(isAssociation).map(umlAssocFromConnection),
      implementations: graph.getLinks().filter(isImplementation).map(umlImplFromConnection)
    };

    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, dbSolution);

    const solution: UmlClassDiagramInput = {
      ...dbSolution,
      classes: dbSolution.classes.map(({classType, name, attributes, methods}) => ({classType, name, attributes, methods})),
    };

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, part, solution}})
      .then(() => setActiveTab('correction'))
      .catch((error) => console.error(error));
  }

  const tabs: Tabs = {
    exerciseText: {
      name: t('exerciseText'),
      render: <UmlDiagramDrawingExerciseTextTabContent exercise={exercise} content={content} correct={() => correct()}
                                                       creatableClassDiagramObjects={creatableClassDiagramObjects}
                                                       onClassClick={(name) => console.warn(name)} toggle={toggle}
                                                       selectedCreatableObject={state.selectedCreatableObject}
                                                       isCorrecting={correctionMutationResult.called && correctionMutationResult.loading}/>
    },
    correction: {
      name: t('correction'),
      render: <WithQuery query={correctionMutationResult} render={(data) => <UmlDiagramDrawingCorrectionTabContent corrResult={data}/>}/>
    }
  };


  return (
    <div className="container is-fluid">

      <div className="columns">
        <div className="column is-three-fifths">

          <div ref={canvas} className="myPaper"/>

          {/*debug &&                   <div className="columns my-3">
              <div className="column is-one-quarter-desktop">
                <label className="label" htmlFor="importFile">Alte Lösung importieren:</label>
              </div>
              <div className="column">
                <input type="file" id="importFile" className="input is-fullwidth" (change)="readFile($event.target.files)">
              </div>
            </div>          */}
        </div>

        <div className="column">

          <BulmaTabs tabs={tabs} activeTabId={activeTab} setActiveTabId={setActiveTab}/>

          {state.editedClass && <>
            <hr/>

            <UmlClassEdit editedClass={state.editedClass} cancelEdit={() => setState((state) => ({...state, editedClass: undefined}))}/>

            {/*< it4all - uml - className - edit [editedClass] = 'editedClass'(cancel) = 'editedClass = undefined' > < /it4all-uml-className-edit>*/}
          </>}

          {/*
  <ng-container *ngIf = 'editedAssociation' >
  < hr >

  < it4all - uml - assoc - edit [editedAssociation] = 'editedAssociation'
  (cancel) = 'editedAssociation = undefined' > < /it4all-uml-assoc-edit>;;;;
</ng-container>
*/}

        </div>
      </div>
    </div>
  );
}
