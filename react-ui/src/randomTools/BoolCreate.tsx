import React, {useState} from 'react';
import {BooleanFormula, generateBooleanFormula} from './bool/_model/bool-formula';
import {displayAssignmentValue, learnerVariable, sampleVariable} from './bool/_model/bool-component-helper';
import {useTranslation} from 'react-i18next';
import {Assignment, calculateAssignments} from './bool/_model/bool-node';
import classNames from 'classnames';
import {RandomSolveButtons} from './RandomToolBase';
import {BoolCreateInstructions} from './BoolCreateInstructions';
import {parseBooleanFormula} from './bool/_model/boolean-formula-parser';

interface IState {
  formula: BooleanFormula;
  assignments: Assignment[];
  corrected: boolean;
  showSampleSolutions: boolean;
  solution: string;
}

function initState(): IState {
  const formula = generateBooleanFormula(sampleVariable);

  const assignments = calculateAssignments(formula.getVariables());

  assignments.forEach((assignment) => {
    assignment[sampleVariable.variable] = formula.evaluate(assignment);
  });

  return {formula, assignments, corrected: false, showSampleSolutions: false, solution: ''};
}

export function BoolCreate(): JSX.Element {

  const {t} = useTranslation('common');
  const [state, setState] = useState(initState());
  const [showInstructions, setShowInstructions] = useState(false);

  function isCorrect(assignment: Assignment): boolean {
    return false;
  }

  function correct(): void {
    const parsed = parseBooleanFormula(state.solution);
    console.error(JSON.stringify(parsed));
  }

  const corrected = false;

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('createBooleanFormula')}</h1>

      <table className="table is-bordered is-fullwidth">
        <thead>
          <tr>
            {state.formula.getVariables().map(({variable}) => <th className="has-text-centered" key={variable}>{variable}</th>)}
            <th className="has-text-centered">{sampleVariable.variable}</th>
            <th className="has-text-centered">{learnerVariable.variable}</th>
          </tr>
        </thead>
        <tbody>
          {state.assignments.map((assignment, index) =>
            <tr key={index}>
              {state.formula.getVariables().map((variable) =>
                <td className="has-text-centered" key={variable.variable}>{displayAssignmentValue(assignment, variable)}</td>
              )}
              <td className="has-text-centered">{displayAssignmentValue(assignment, sampleVariable)}</td>
              <td className={classNames({
                'has-background-danger': corrected && !isCorrect(assignment),
                'has-background-success': corrected && isCorrect(assignment),
                'has-text-white': corrected
              }, 'has-text-centered')}>
                {displayAssignmentValue(assignment, learnerVariable)}
                &nbsp;
                {corrected && !isCorrect(assignment) && <span className="has-text-white">&#10008;</span>}
                {corrected && !isCorrect(assignment) && <span className="has-text-white">&#10004;</span>}
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <hr/>

      <div className="field has-addons">
        <div className="control">
          <label htmlFor="solution" className="button is-static">{sampleVariable.variable} = </label>
        </div>
        <div className="control is-expanded">
          {/*[(ngModel)]="solution"*/}
          <input type="text" defaultValue={state.solution} onChange={(event) => setState((state) => ({...state, solution: event.target.value}))}
                 className="input" id="solution" autoComplete="off" placeholder="Boolesche Formel" autoFocus/>
        </div>
      </div>


      <RandomSolveButtons correct={correct} nextExercise={() => setState(initState())}/>


      <div className="columns my-3">
        <div className="column is-half-desktop">
          <button className="button is-primary is-fullwidth"
                  onClick={() => setState(({showSampleSolutions, ...rest}) => ({...rest, showSampleSolutions: !showSampleSolutions}))}>
            Musterlösungen anzeigen
          </button>
        </div>
        <div className="column is-half-desktop">
          <button className="button is-info is-fullwidth" onClick={() => setShowInstructions((showInstructions) => !showInstructions)}>
            Hilfe anzeigen
          </button>
        </div>
      </div>

      {state.showSampleSolutions && <div className="card">
        <header className="card-header">
          <p className="card-header-title is-centered">Musterlösungen</p>
        </header>
        <div className="card-content">
          <code>{state.formula.asString()}</code>
        </div>
      </div>}


      {showInstructions && <BoolCreateInstructions/>}

    </div>
  );
}
