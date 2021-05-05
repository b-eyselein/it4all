import React, {useState} from "react";
import {BooleanFormula, generateBooleanFormula} from "./bool/_model/bool-formula";
import {displayAssignmentValue, isCorrect, learnerVariable, sampleVariable} from "./bool/_model/bool-component-helper";
import classNames from "classnames";
import {RandomSolveButtons} from "./RandomToolBase";
import {BooleanNode, calculateAssignments} from "./bool/_model/bool-node";

type Assignment = Map<string, boolean>;

interface IState {
  formula: BooleanFormula;
  assignments: Assignment[];
  subFormulas: BooleanNode[];
  withSubFormulas: boolean;
}

function initState(formula: BooleanFormula, withSubFormulas: boolean = false): IState {
  const subFormulas = formula.getSubFormulas();

  const assignments = calculateAssignments(formula.getVariables());

  assignments.forEach((assignment) => {
    assignment.set(sampleVariable.variable, formula.evaluate(assignment));
    assignment.set(learnerVariable.variable, false);
  });

  return {formula, assignments, subFormulas, withSubFormulas};
}

export function BoolFillout(): JSX.Element {

  const corrected = false;
  const completelyCorrect = false;

  const [state, setState] = useState<IState>(initState(generateBooleanFormula(sampleVariable), false));

  function updateAssignment(assignment: Assignment): void {
    setState((state) => {
      return {...state};
    });
  }

  function correct(): void {

  }

  function nextExercise(): void {

  }

  return (
    <div className="columns">
      <div className="column is-half-desktop is-offset-one-quarter-desktop">

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
          <h2 className="subtitle is-4 has-text-centered"><code>{state.formula.asHtmlString()}</code></h2>
          <h3 className="subtitle is-5 has-text-centered has-text-grey">{state.formula.asString()}</h3>
        </div>

        <table className="table is-bordered is-fullwidth my-3">
          <thead>
            <tr>
              {state.formula.getVariables().map(({variable}) =>
                <th className="has-text-centered" key={variable}>{variable}</th>
              )}
              {state.withSubFormulas && state.subFormulas.map((subFormula) => <th
                className="has-text-centered">{subFormula.asHtmlString()}</th>)}
              <th className="has-text-centered">{learnerVariable.variable}</th>
            </tr>
          </thead>
          <tbody>
            {state.assignments.map((assignment, index) =>
              <tr key={index}>
                {state.formula.getVariables().map((variable) =>
                  <td className="has-text-centered"
                      key={variable.variable}>{displayAssignmentValue(assignment, variable)}</td>
                )}
                {state.withSubFormulas && state.subFormulas.map((subFormula) =>
                  <th>{/* FIXME: button for subFormula... */}</th>)}

                <td className={classNames({
                  'has-background-danger': corrected && !isCorrect(assignment),
                  'has-background-success': corrected && isCorrect(assignment)
                }, 'has-text-centered')}>
                  <button onClick={() => updateAssignment(assignment)}
                          className={classNames('button', {'is-link': assignment.get(learnerVariable.variable)})}>
                    {displayAssignmentValue(assignment, learnerVariable)}
                  </button>
                  &nbsp;
                  {corrected && (isCorrect(assignment) ? <span>&#10004;</span> : <span>&#10008;</span>)}
                </td>
              </tr>
            )}
          </tbody>
        </table>

        {corrected &&
        <div
          className={classNames('notification', 'has-text-centered', completelyCorrect ? 'is-success' : 'is-danger')}>
          (completelyCorrect
          ? <span>&#10004; Ihre Lösung ist korrekt.</span>
          : <span>&#10008; Ihre Lösung ist nicht (komplett) korrekt.</span>)
        </div>}

        <RandomSolveButtons correct={correct} nextExercise={nextExercise}/>
      </div>
    </div>
  );
}
