import {Fragment, useState} from 'react';
import {RandomSolveButtons} from './RandomSolveButtons';
import {BooleanNode, evaluate, getSubNodes, getVariables, stringifyNode} from './boolModel/boolNode';
import {Assignment, calculateAssignments, displayAssignmentValue, isCorrect, learnerVariable, sampleVariable} from './boolModel/assignment';
import {useTranslation} from 'react-i18next';
import {BoolFormulaDisplay} from './BoolFormulaDisplay';
import classNames from 'classnames';
import update from 'immutability-helper';
import {generateBooleanFormula} from './boolModel/booleanFormulaGenerator';
import {BoolTable, BoolTableColumn} from './BoolTable';
import {bgColors} from '../consts';

interface IState {
  formula: BooleanNode;
  assignments: Assignment[];
  subFormulas: BooleanNode[];
  withSubFormulas: boolean;
  corrected: boolean;
}

function initState(withSubFormulas = false): IState {
  const formula = generateBooleanFormula();

  const assignments = calculateAssignments(getVariables(formula));

  assignments.forEach((assignment) => {
    assignment[sampleVariable.variable] = evaluate(formula, assignment);
    assignment[learnerVariable.variable] = false;
  });

  return {formula, assignments, subFormulas: getSubNodes(formula), withSubFormulas, corrected: false};
}

export function BoolFillOut(): JSX.Element {

  const {t} = useTranslation('common');
  const [state, setState] = useState<IState>(initState());

  function updateAssignment(assignmentIndex: number): void {
    setState((state) => update(state, {assignments: {[assignmentIndex]: {$toggle: [learnerVariable.variable]}}}));
  }

  function correct(): void {
    setState((state) => update(state, {corrected: {$set: true}}));
  }

  const completelyCorrect = state.assignments.every(isCorrect);
  const variables = getVariables(state.formula);

  const columns: BoolTableColumn[] = [
    ...variables.map((node) => ({node})),
    // TODO: subFormulas...?
    {
      node: sampleVariable,
      children: (node, assignment) => state.corrected ? <>{displayAssignmentValue(assignment, node)}</> : <Fragment/>
    },
    {
      node: learnerVariable,
      trClasses: (node, assignment) => ({
        [bgColors.inCorrect]: state.corrected && !isCorrect(assignment),
        [bgColors.correct]: state.corrected && isCorrect(assignment),
        'text-white': state.corrected
      }),
      children: (node, assignment, assignmentIndex) => {

        const classes = classNames('px-4', 'py-2', 'rounded', 'border',
          assignment[learnerVariable.variable] ? ['bg-blue-600', 'border-blue-600', 'text-white'] : ['border-slate-500']);

        return (
          <>
            <button onClick={() => updateAssignment(assignmentIndex)} className={classes}>
              {displayAssignmentValue(assignment, learnerVariable)}
            </button>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            {state.corrected && (isCorrect(assignment) ? <span>&#10004;</span> : <span>&#10008;</span>)}
          </>
        );
      }
    }
  ];

  return (
    <div className="container mx-auto">
      <h1 className="mb-4 font-bold text-2xl text-center">{t('fillOutTruthTable')}</h1>

      {/*<div className="field">
        <div className="control has-text-centered">
          <label className="checkbox">
            <input type="checkbox" [(ngModel)]="withSubFormulas"> Mit Zwischenschritten
          </label>
        </div>
      </div>*/}

      <div className="my-3">
        <h2 className="text-xl text-center">
          <BoolFormulaDisplay left={sampleVariable} right={state.formula}/>
        </h2>
        <h3 className="text-center text-gray-500">{sampleVariable.variable} = {stringifyNode(state.formula)}</h3>
      </div>

      <BoolTable columns={columns} assignments={state.assignments}/>

      {state.corrected && <div className={classNames('my-4', 'p-2', 'rounded', 'text-center', 'text-white', completelyCorrect ? 'bg-green-500' : 'bg-red-500')}>
        {completelyCorrect
          ? <>&#10004; {t('solutionCorrect')}.</>
          : <>&#10008; {t('solutionNotCorrect')}.</>}
      </div>}

      <RandomSolveButtons toolId={'bool'} correct={correct} nextExercise={() => setState(initState())}/>
    </div>
  );
}
