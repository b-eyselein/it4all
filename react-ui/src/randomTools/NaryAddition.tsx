import React, {useState} from 'react';
import {RandomSolveButtons} from './RandomToolBase';
import classNames from 'classnames';
import {NaryNumberReadOnlyInputComponent} from './NaryNumberReadOnlyInput';
import {allNumberingSystems, BINARY_SYSTEM, maximalMax, minimalMax, NaryReadOnlyNumberInput, NumberingSystem} from './nary';
import {randomInt} from './bool/_model/bool-formula';
import {useTranslation} from 'react-i18next';

const firstSummandFieldId = 'firstSummand';
const secondSummandFieldId = 'secondSummand';

interface IState {
  system: NumberingSystem;
  target: number,
  firstSummand: NaryReadOnlyNumberInput,
  secondSummand: NaryReadOnlyNumberInput,
  checked: boolean;
  correct: boolean;
  solutionString: string,
}

function generateExercise(max: number, system: NumberingSystem = BINARY_SYSTEM): IState {
  const target = randomInt(1, max);

  const firstSummandDecimal = randomInt(1, target);

  const firstSummand: NaryReadOnlyNumberInput = {
    decimalNumber: firstSummandDecimal,
    fieldId: firstSummandFieldId,
    maxValueForDigits: max,
    labelContent: 'Summand 1',
    numberingSystem: system
  };

  const secondSummand: NaryReadOnlyNumberInput = {
    decimalNumber: target - firstSummandDecimal,
    fieldId: secondSummandFieldId,
    maxValueForDigits: max,
    labelContent: 'Summand 2',
    numberingSystem: system
  };

  return {system, target, firstSummand, secondSummand, checked: false, correct: false, solutionString: ''};
}

export function NaryAddition(): JSX.Element {

  const {t} = useTranslation('common');
  const [max, setMax] = useState(256);
  const [state, setState] = useState(generateExercise(max));

  function updateNumberingSystem(/*event: ChangeEvent<HTMLSelectElement>*/): void {
    console.info('TODO: update...');
  }

  function nextExercise(): void {
    setState((state) => generateExercise(max, state.system));
  }

  function checkSolution(): void {
    console.info('TODO');
    /*
    const reversedSolutionString: string = state.solutionString
      .replace(/\s/g, '')
      .split('')
      .reverse()
      .join('');

    const solution: number = parseInt(reversedSolutionString, state.system.radix);

    const checked = true;

    const correct = solution === state.target;
     */
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
                <button className="button" onClick={() => setMax(max / 2)} disabled={max === minimalMax}>/ 2</button>
              </div>
              <div className="control is-expanded">
                <input className="input has-text-centered" type="number" id="max" value={max} readOnly/>
              </div>
              <div className="control">
                <button className="button" onClick={() => setMax(max * 2)} disabled={max === maximalMax}>* 2</button>
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
        <NaryNumberReadOnlyInputComponent naryNumberInput={state.firstSummand}/>
      </div>

      <div className="my-3">
        <NaryNumberReadOnlyInputComponent naryNumberInput={state.secondSummand}/>
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
            {/*ngModel="solutionString"*/}
            <input type="text" id="solution" placeholder=" Lösung" autoFocus autoComplete="off"
                   className={classNames('input', {
                     'is-success': state.checked && state.correct,
                     'is-danger': state.checked && !state.correct
                   })}/>
          </div>
          <div className=" control">
            <div className=" button is-static">
              <sub>{state.system.radix}</sub>
            </div>
          </div>
        </div>
      </div>

      {state.checked && <div className={classNames('notification', 'has-text-centered', state.correct ? 'is-success' : 'is-danger')}>
        {state.correct
          ? <span>&#10008; Die Lösung ist nicht korrekt.</span>
          : <span>&#10004; Die Lösung ist korrekt.</span>
        }
      </div>}

      <RandomSolveButtons correct={checkSolution} nextExercise={nextExercise}/>

    </div>
  );
}
