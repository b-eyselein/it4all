import React, {ChangeEvent, useState} from 'react';
import {RandomSolveButtons} from './RandomToolBase';
import classNames from 'classnames';
import {NaryNumberReadOnlyInputComponent, NaryReadOnlyNumberInput} from './NaryNumberReadOnlyInput';
import {allNumberingSystems, BINARY_SYSTEM, maximalMax, minimalMax, NumberingSystem} from './nary';
import {randomInt} from './boolModel/bool-formula';
import {useTranslation} from 'react-i18next';

interface IState {
  max: number;
  numberingSystem: NumberingSystem;
  target: number,
  firstSummand: NaryReadOnlyNumberInput,
  secondSummand: NaryReadOnlyNumberInput,
  checked: boolean;
  correct: boolean;
  solutionString: string,
}

function generateExercise(maxValueForDigits = 256, numberingSystem: NumberingSystem = BINARY_SYSTEM): IState {
  const target = randomInt(1, maxValueForDigits);

  const firstSummandDecimal = randomInt(1, target);

  const firstSummand: NaryReadOnlyNumberInput = {decimalNumber: firstSummandDecimal, maxValueForDigits, numberingSystem};

  const secondSummand: NaryReadOnlyNumberInput = {decimalNumber: target - firstSummandDecimal, maxValueForDigits, numberingSystem};

  return {max: maxValueForDigits, numberingSystem, target, firstSummand, secondSummand, checked: false, correct: false, solutionString: ''};
}

export function NaryAddition(): JSX.Element {

  const {t} = useTranslation('common');
  const [state, setState] = useState(generateExercise());

  function updateNumberingSystem(event: ChangeEvent<HTMLSelectElement>): void {
    const newRadix = parseInt(event.target.value);

    const numberingSystem = allNumberingSystems.find(({radix}) => radix === newRadix);

    if (numberingSystem) {
      setState(({max}) => generateExercise(max, numberingSystem));
    }
  }

  function nextExercise(): void {
    setState((state) => generateExercise(state.max, state.numberingSystem));
  }

  function checkSolution(): void {
    const reversedSolutionString: string = state.solutionString
      .replace(/\s/g, '')
      .split('')
      .reverse()
      .join('');

    const solution: number = parseInt(reversedSolutionString, state.numberingSystem.radix);

    setState((state) => ({...state, checked: true, correct: solution === state.target}));
  }

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('naryAddition')}</h1>

      <div className="columns">
        <div className="column">
          <div className="field">
            <label className="label has-text-centered" htmlFor="max">Maximalwert</label>
            <div className="field has-addons">
              <div className="control">
                <button className="button" onClick={() => setState((state) => generateExercise(state.max / 2))} disabled={state.max === minimalMax}>
                  / 2
                </button>
              </div>
              <div className="control is-expanded">
                <input className="input has-text-centered" type="number" id="max" value={state.max} readOnly/>
              </div>
              <div className="control">
                <button className="button" onClick={() => setState((state) => generateExercise(state.max * 2))} disabled={state.max === maximalMax}>
                  * 2
                </button>
              </div>
            </div>
          </div>
        </div>
        <div className="column">
          <div className="field">
            <label htmlFor="numberSystem" className=" label has-text-centered">Zahlensystem</label>
            <div className=" control">
              <div className=" select is-fullwidth">
                {/*ngModel="system"*/}
                <select onChange={updateNumberingSystem} id="numberSystem">
                  {allNumberingSystems.map((ns) => <option key={ns.radix} value={ns.radix}>{ns.radix} - {ns.name}</option>)}
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>

      <hr/>

      <div className="my-3">
        <NaryNumberReadOnlyInputComponent labelContent={'Summand 1'} naryNumberInput={state.firstSummand}/>
      </div>

      <div className="my-3">
        <NaryNumberReadOnlyInputComponent labelContent={'Summand 2'} naryNumberInput={state.secondSummand}/>
      </div>

      <hr/>

      <div className="field">
        <div className="field has-addons">
          <div className="control">
            <div className="button is-static">
              <label htmlFor="solution">Lösung:</label>
            </div>
          </div>
          <div className="control is-expanded">
            <input type="text" id="solution" placeholder=" Lösung" autoFocus autoComplete="off" defaultValue={state.solutionString}
                   style={{direction: 'rtl', unicodeBidi: 'bidi-override'}}
                   className={classNames('input', {'is-success': state.checked && state.correct, 'is-danger': state.checked && !state.correct})}
                   onChange={(event) => setState((state) => ({...state, solutionString: event.target.value}))}/>
          </div>
          <div className=" control">
            <div className=" button is-static">
              <sub>{state.numberingSystem.radix}</sub>
            </div>
          </div>
        </div>
      </div>

      {state.checked && <div className={classNames('notification', 'has-text-centered', state.correct ? 'is-success' : 'is-danger')}>
        {state.correct ? <span>&#10004;</span> : <span>&#10008;</span>} Die Lösung ist {state.correct ? '' : ' nicht'} korrekt.
      </div>}

      <RandomSolveButtons correct={checkSolution} nextExercise={nextExercise}/>

    </div>
  );
}
