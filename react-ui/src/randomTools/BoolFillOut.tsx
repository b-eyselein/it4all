import {useState} from 'react';
import {BooleanFormula, generateBooleanFormula} from './boolModel/bool-formula';
import {displayAssignmentValue, isCorrect, learnerVariable, sampleVariable} from './boolModel/bool-component-helper';
import classNames from 'classnames';
import {RandomSolveButtons} from './RandomSolveButtons';
import {Assignment, BooleanNode, calculateAssignments} from './boolModel/bool-node';
import {useTranslation} from 'react-i18next';
import update from 'immutability-helper';

interface IState {
  formula: BooleanFormula;
  assignments: Assignment[];
  subFormulas: BooleanNode[];
  withSubFormulas: boolean;
  corrected: boolean;
}

function initState(withSubFormulas = false): IState {

  const formula = generateBooleanFormula(sampleVariable);

  const subFormulas = formula.getSubFormulas();

  const assignments = calculateAssignments(formula.getVariables());

  assignments.forEach((assignment) => {
    assignment[sampleVariable.variable] = formula.evaluate(assignment);
    assignment[learnerVariable.variable] = false;
  });

  return {formula, assignments, subFormulas, withSubFormulas, corrected: false};
}

const cellClasses = ['p-2 text-center border border-slate-200'];

const cellClassName = classNames(cellClasses);

export function BoolFillOut(): JSX.Element {

  const {t} = useTranslation('common');
  const [state, setState] = useState<IState>(initState());

  function updateAssignment(assignmentIndex: number): void {
    setState((state) => update(state, {assignments: {[assignmentIndex]: {$toggle: [learnerVariable.variable]}}}));
  }

  function correct(): void {
    setState((state) => ({...state, corrected: true}));
  }

  const completelyCorrect = state.assignments.every(isCorrect);

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
        <h2 className="text-xl text-center" dangerouslySetInnerHTML={{__html: `<code>${state.formula.asHtmlString()}</code>`}}/>
        <h3 className="text-center text-gray-500">{state.formula.asString()}</h3>
      </div>

      <table className="w-full">
        <thead>
          <tr>
            {state.formula.getVariables().map(({variable}) =>
              <th key={variable} className={cellClassName}>{variable}</th>
            )}
            {state.withSubFormulas && state.subFormulas.map((subFormula) =>
              <th key={subFormula.asString()} className={cellClassName}>{subFormula.asHtmlString()}</th>
            )}
            <th className={cellClassName}>{learnerVariable.variable}</th>
          </tr>
        </thead>
        <tbody>
          {state.assignments.map((assignment, index) =>
            <tr key={index}>
              {state.formula.getVariables().map((variable) =>
                <td key={variable.variable} className={cellClassName}>{displayAssignmentValue(assignment, variable)}</td>
              )}
              {state.withSubFormulas && state.subFormulas.map((subFormula, index) =>
                <th key={index}>{/* FIXME: button for subFormula... */}</th>
              )}

              <td className={classNames(cellClasses, {
                'bg-red-500': state.corrected && !isCorrect(assignment),
                'bg-green-500': state.corrected && isCorrect(assignment)
              })}>
                <button onClick={() => updateAssignment(index)}
                        className={classNames('px-4', 'py-2', 'rounded', 'border', assignment[learnerVariable.variable] ? ['bg-blue-600', 'border-blue-600', 'text-white'] : ['border-slate-500'])}>
                  {displayAssignmentValue(assignment, learnerVariable)}
                </button>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                {state.corrected && (isCorrect(assignment) ? <span className="text-white">&#10004;</span> : <span className="text-white">&#10008;</span>)}
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {state.corrected && <div className={classNames('my-4', 'p-2', 'text-center', 'text-white', completelyCorrect ? 'bg-green-500' : 'bg-red-500')}>
        {completelyCorrect
          ? <>&#10004; {t('solutionCorrect')}.</>
          : <>&#10008; {t('solutionNotCorrect')}.</>}
      </div>}

      <RandomSolveButtons toolId={'bool'} correct={correct} nextExercise={() => setState(initState())}/>
    </div>
  );
}
