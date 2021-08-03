import React, {useState} from 'react';
import {RandomSolveButtons} from './RandomToolBase';
import {NaryNumberReadOnlyInputComponent, NaryReadOnlyNumberInput} from './NaryNumberReadOnlyInput';
import {BINARY_SYSTEM, NaryIState, NumberingSystem} from './nary';
import {randomInt} from './boolModel/bool-formula';
import {useTranslation} from 'react-i18next';
import {NaryLimits} from './NaryLimits';
import {NaryNumberInput} from './NaryNumberInput';
import {NaryNumberingSystemSelect} from './NaryNumberingSystemSelect';

interface IState extends NaryIState {
  numberingSystem: NumberingSystem;
  target: number,
  firstSummand: NaryReadOnlyNumberInput,
  secondSummand: NaryReadOnlyNumberInput,
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
          <NaryLimits max={state.max} update={(newValue) => setState(generateExercise(newValue))}/>
        </div>
        <div className="column">
          <NaryNumberingSystemSelect label={t('numberingSystem')} system={state.numberingSystem}
                                     update={(newSystem) => setState(({max}) => generateExercise(max, newSystem))}/>
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

      <NaryNumberInput labelContent={t('solution')} initialValue={state.solutionString} checked={state.checked} correct={state.correct}
                       radix={state.numberingSystem.radix} update={(newValue) => setState((state) => ({...state, solutionString: newValue}))}/>

      <RandomSolveButtons correct={checkSolution} nextExercise={nextExercise}/>

    </div>
  );
}
