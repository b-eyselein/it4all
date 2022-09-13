import {useState} from 'react';
import {BooleanFormula, generateBooleanFormula} from './boolModel/bool-formula';
import {displayAssignmentValue, isCorrect, learnerVariable, sampleVariable} from './boolModel/bool-component-helper';
import {useTranslation} from 'react-i18next';
import {Assignment, BooleanNode, calculateAssignments} from './boolModel/bool-node';
import classNames from 'classnames';
import {RandomSolveButtons} from './RandomSolveButtons';
import {BoolCreateInstructions} from './BoolCreateInstructions';
import {parseBooleanFormulaFromLanguage} from './boolModel/boolean-formula-parser';
import {Result} from 'parsimmon';
import update from 'immutability-helper';

interface IState {
  formula: BooleanFormula;
  assignments: Assignment[];
  corrected: boolean;
  showSampleSolutions: boolean;
  solution: string;
  userSolutionFormula?: BooleanNode;
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

  function correct(): void {
    const parsed: Result<BooleanNode> = parseBooleanFormulaFromLanguage(state.solution);

    if (parsed.status) {
      const userSolutionFormula = parsed.value;

      setState(({assignments, ...rest}) => {
        assignments.forEach((a) => a[learnerVariable.variable] = userSolutionFormula.evaluate(a));

        return {...rest, userSolutionFormula, assignments, corrected: true};
      });
    } else {
      setState((state) => update(state, {userSolutionFormula: {$set: undefined}, corrected: {$set: true}}));
    }
  }

  return (
    <div className="container mx-auto">
      <h1 className="mb-4 font-bold text-2xl text-center">{t('createBooleanFormula')}</h1>

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
                'has-background-danger': state.corrected && !isCorrect(assignment),
                'has-background-success': state.corrected && isCorrect(assignment),
                'has-text-white': state.corrected
              }, 'has-text-centered')}>
                {displayAssignmentValue(assignment, learnerVariable)}
                &nbsp;
                {state.corrected && !isCorrect(assignment) && <span className="has-text-white">&#10008;</span>}
                {state.corrected && !isCorrect(assignment) && <span className="has-text-white">&#10004;</span>}
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
          <input type="text" value={state.solution} onChange={(event) => setState((state) => ({...state, solution: event.target.value}))}
                 className="input" id="solution" autoComplete="off" placeholder="Boolesche Formel" autoFocus/>
        </div>
      </div>

      <RandomSolveButtons toolId={'bool'} correct={correct} nextExercise={() => setState(initState())}/>

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
