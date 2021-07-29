import React, {useState} from 'react';
import {BooleanFormula, generateBooleanFormula} from './bool/_model/bool-formula';
import {displayAssignmentValue, isCorrect, learnerVariable, sampleVariable} from './bool/_model/bool-component-helper';
import classNames from 'classnames';
import {RandomSolveButtons} from './RandomToolBase';
import {Assignment, BooleanNode, calculateAssignments} from './bool/_model/bool-node';
import {useTranslation} from 'react-i18next';

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

export function BoolFillOut(): JSX.Element {

  const {t} = useTranslation('common');
  const [state, setState] = useState<IState>(initState());

  function updateAssignment(assignmentIndex: number): void {
    setState(({assignments, ...rest}) => {
      return {
        ...rest,
        assignments: assignments.map((a, index) => {
          return assignmentIndex === index
            ? {...a, [learnerVariable.variable]: !a[learnerVariable.variable]}
            : a;
        })
      };
    });
  }

  function correct(): void {
    setState((state) => ({...state, corrected: true}));
  }

  const completelyCorrect = state.assignments.every(isCorrect);

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('fillOutTruthTable')}</h1>

      {/*
      <!--      <div className="field">-->
      <!--        <div className="control has-text-centered">-->
      <!--          <label className="checkbox">-->
      <!--            <input type="checkbox" [(ngModel)]="withSubFormulas"> Mit Zwischenschritten-->
      <!--          </label>-->
      <!--        </div>-->
      <!--      </div>-->
      */}

      <div className="my-3">
        <h2 className="subtitle is-4 has-text-centered" dangerouslySetInnerHTML={{__html: `<code>${state.formula.asHtmlString()}</code>`}}/>
        <h3 className="subtitle is-5 has-text-centered has-text-grey">{state.formula.asString()}</h3>
      </div>

      <table className="table is-bordered is-fullwidth my-3">
        <thead>
          <tr>
            {state.formula.getVariables().map(({variable}) =>
              <th className="has-text-centered" key={variable}>{variable}</th>
            )}
            {state.withSubFormulas && state.subFormulas.map((subFormula) =>
              <th key={subFormula.asString()} className="has-text-centered">{subFormula.asHtmlString()}</th>
            )}
            <th className="has-text-centered">{learnerVariable.variable}</th>
          </tr>
        </thead>
        <tbody>
          {state.assignments.map((assignment, index) =>
            <tr key={index}>
              {state.formula.getVariables().map((variable) =>
                <td className="has-text-centered" key={variable.variable}>{displayAssignmentValue(assignment, variable)}</td>
              )}
              {state.withSubFormulas && state.subFormulas.map((subFormula, index) =>
                <th key={index}>{/* FIXME: button for subFormula... */}</th>)}

              <td className={classNames({
                'has-background-danger': state.corrected && !isCorrect(assignment),
                'has-background-success': state.corrected && isCorrect(assignment)
              }, 'has-text-centered')}>
                <button onClick={() => updateAssignment(index)} className={classNames('button', {'is-link': assignment[learnerVariable.variable]})}>
                  {displayAssignmentValue(assignment, learnerVariable)}
                </button>
                &nbsp;
                {state.corrected && (isCorrect(assignment) ? <span>&#10004;</span> : <span>&#10008;</span>)}
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {state.corrected && <div
        className={classNames('notification', 'has-text-centered', completelyCorrect ? 'is-success' : 'is-danger')}>
        {completelyCorrect
          ? <span>&#10004; Ihre Lösung ist korrekt.</span>
          : <span>&#10008; Ihre Lösung ist nicht (komplett) korrekt.</span>}
      </div>}

      <RandomSolveButtons correct={correct} nextExercise={() => setState(initState())}/>
    </div>
  );
}
