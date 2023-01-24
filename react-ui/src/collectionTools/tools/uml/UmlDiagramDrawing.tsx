import {useEffect, useMemo, useRef, useState} from 'react';
import * as joint from 'jointjs';
import {
  ExerciseSolveFieldsFragment,
  UmlClassDiagramInput,
  UmlClassInput,
  UmlExerciseContentFragment,
  UmlExPart,
  UmlMultiplicity,
  useUmlCorrectionMutation
} from '../../../graphql';
import {NewTabs} from '../../../helpers/BulmaTabs';
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
import {UmlAssocEdit} from './UmlAssocEdit';
import update from 'immutability-helper';

export const enum CreatableClassDiagramObject {
  Class = 'Class',
  Association = 'Association',
  Implementation = 'Implementation'
}

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
  part: UmlExPart;
  oldSolution: UmlDbClassDiagram | undefined;
}

interface IState {
  markedClass?: MyJointClass;
  editedClass?: MyJointClass;
  editedAssociation?: joint.shapes.uml.Association;
  selectedCreatableObject?: CreatableClassDiagramObject;
}

export function UmlDiagramDrawing({exercise, content, withHelp, partId, part, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const canvas = useRef<HTMLDivElement>(null);

  const [activeTab, setActiveTab] = useState('exerciseText');
  const [state, setState] = useState<IState>({});

  const [correctExercise, correctionMutationResult] = useUmlCorrectionMutation();

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
      if (state.selectedCreatableObject && state.selectedCreatableObject === CreatableClassDiagramObject.Class) {
        addClassToGraph(graph, 'Klasse 1', [], [], {x, y});
      }

      return state;
    }));

    paper.on('cell:pointerclick', (cellView) => setState((state) => {

        const model = cellView.model;

        if (!(model instanceof MyJointClass)) {
          return update(state, {markedClass: {$set: undefined}});
        }

        console.info(cellView.model.id);
        console.info(state.markedClass);

        if (state.markedClass === undefined) {
          // Nothing set yet
          cellView.highlight();
          return update(state, {markedClass: {$set: model}});
        }

        if (state.markedClass.getClassName() === model.getClassName()) {
          // class was clicked again
          cellView.unhighlight();
          return update(state, {markedClass: {$set: undefined}});
        }

        console.info(state.selectedCreatableObject);

        if (state.selectedCreatableObject !== undefined) {
          switch (state.selectedCreatableObject) {
            case CreatableClassDiagramObject.Association:
              addAssociationToGraph(graph, state.markedClass, UmlMultiplicity.Unbound, model, UmlMultiplicity.Unbound);
              break;
            case CreatableClassDiagramObject.Implementation:
              addImplementationToGraph(graph, state.markedClass, model);
              break;
          }

          state.markedClass.findView(paper).unhighlight();

          return update(state, {markedClass: {$set: undefined}});
        }

        // New class selected, nothing to create
        state.markedClass.findView(paper).unhighlight();
        cellView.highlight();
        return update(state, {markedClass: {$set: model}});
      })
    );

    paper.on('cell:contextmenu', (cellView) => setState((state) => {
      const model = cellView.model;

      if (model instanceof joint.shapes.uml.Association) {
        return update(state, {
          editedAssociation: {
            $set: state.editedAssociation === undefined || state.editedAssociation.id !== model.id
              ? model
              : undefined
          }
        });
      }

      if (model instanceof MyJointClass && !withHelp) {
        return update(state, {
          editedClass: {
            $set: state.editedClass === undefined || state.editedClass.id !== model.id
              ? model
              : undefined
          }
        });
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

  const selectCreatableObject = (x: CreatableClassDiagramObject): void =>
    setState((state) => update(state, {selectedCreatableObject: {$set: x === state.selectedCreatableObject ? undefined : x}}));

  function correct(): void {
    const dbSolution: UmlDbClassDiagram = {
      classes: graph.getCells().filter(isMyJointClass).map((cell) => cell.getAsUmlClass()),
      associations: graph.getLinks().filter(isAssociation).map(umlAssocFromConnection),
      implementations: graph.getLinks().filter(isImplementation).map(umlImplFromConnection)
    };

    database.upsertSolutionWithParts(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, dbSolution);

    const solution: UmlClassDiagramInput = {
      ...dbSolution,
      classes: dbSolution.classes.map(({classType, name, attributes, methods}) => ({classType, name, attributes, methods})),
    };

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, part, solution}})
      .then(() => setActiveTab('correction'))
      .catch((error) => console.error(error));
  }

  return (
    <div className="px-2">

      <div className="grid grid-cols-5 gap-2">
        <div className="col-span-3">

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

        <div className="col-span-2">
          <NewTabs activeTabId={activeTab} setActiveTabId={setActiveTab}>
            {{
              exerciseText: {
                name: t('exerciseText'),
                render: (
                  <UmlDiagramDrawingExerciseTextTabContent
                    exercise={exercise}
                    content={content}
                    correct={correct}
                    creatableClassDiagramObjects={creatableClassDiagramObjects}
                    onClassClick={(name) => console.warn(name)}
                    toggle={selectCreatableObject}
                    selectedCreatableObject={state.selectedCreatableObject}
                    /* isCorrecting={correctionMutationResult.called && correctionMutationResult.loading}*/
                    part={part}/>
                )
              },
              correction: {
                name: t('correction'),
                render: (
                  <WithQuery query={correctionMutationResult}>
                    {(data) => <UmlDiagramDrawingCorrectionTabContent corrResult={data}/>}
                  </WithQuery>
                )
              }
            }}
          </NewTabs>

          {state.editedClass &&
            <UmlClassEdit editedClass={state.editedClass} cancelEdit={() => setState((state) => update(state, {editedClass: {$set: undefined}}))}/>}

          {state.editedAssociation &&
            <UmlAssocEdit editedAssociation={state.editedAssociation}
                          cancelEdit={() => setState((state) => update(state, {editedAssociation: {$set: undefined}}))}/>}

        </div>
      </div>
    </div>
  );
}
