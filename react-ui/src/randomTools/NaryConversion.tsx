import React, {useState} from 'react';
import {BINARY_SYSTEM, HEXADECIMAL_SYSTEM, NaryIState, NumberingSystem} from './nary';
import {NaryNumberReadOnlyInputComponent, NaryReadOnlyNumberInput} from './NaryNumberReadOnlyInput';
import {RandomSolveButtons} from './RandomSolveButtons';
import {randomInt} from './boolModel/bool-formula';
import {useTranslation} from 'react-i18next';
import {NaryLimits} from './NaryLimits';
import {NaryNumberInput} from './NaryNumberInput';
import {NaryNumberingSystemSelect} from './NaryNumberingSystemSelect';

interface IState extends NaryIState {
  toConvertInput: NaryReadOnlyNumberInput;
  startSystem: NumberingSystem;
  targetSystem: NumberingSystem;
}

function generateExercise(max = 256, startSystem: NumberingSystem = BINARY_SYSTEM, targetSystem: NumberingSystem = HEXADECIMAL_SYSTEM): IState {

  const decimalNumber = randomInt(1, max);

  const toConvertInput: NaryReadOnlyNumberInput = {
    decimalNumber,
    numberingSystem: startSystem,
    maxValueForDigits: max
  };

  return {max, toConvertInput, startSystem, targetSystem, solutionString: '', checked: false, correct: false};
}

export function NaryConversion(): JSX.Element {

  const {t} = useTranslation('common');
  const [state, setState] = useState<IState>(generateExercise());

  function updateStartSystem(newSystem: NumberingSystem): void {
    setState((state) => generateExercise(state.max, newSystem, state.targetSystem));
  }

  function updateTargetSystem(newSystem: NumberingSystem): void {
    setState((state) => generateExercise(state.max, state.startSystem, newSystem));
  }

  function correct(): void {
    const solution = parseInt(state.solutionString, state.targetSystem.radix);

    setState((state) => ({...state, checked: true, correct: solution === state.toConvertInput.decimalNumber}));
  }

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('naryConversion')}</h1>

      <NaryLimits max={state.max} update={(newValue) => setState((state) => generateExercise(newValue, state.startSystem, state.targetSystem))}/>

      <div className="columns">
        <div className="column is-half-desktop">
          <NaryNumberingSystemSelect label={t('startSystem')} system={state.startSystem} update={(newSystem) => updateStartSystem(newSystem)}/>
        </div>
        <div className="column is-half-desktop">
          <NaryNumberingSystemSelect label={t('targetSytem')} system={state.targetSystem} update={(newSystem) => updateTargetSystem(newSystem)}/>
        </div>
      </div>

      <hr/>

      <NaryNumberReadOnlyInputComponent labelContent={t('startNumber')} naryNumberInput={state.toConvertInput}/>

      <hr/>

      <NaryNumberInput labelContent={t('solution')} initialValue={state.solutionString} checked={state.checked} correct={state.correct}
                       radix={state.targetSystem.radix} update={(newValue) => setState((state) => ({...state, solutionString: newValue}))}/>

      <div className="my-3">
        <RandomSolveButtons toolId={'nary'} correct={correct}
                            nextExercise={() => setState(({max, startSystem, targetSystem}) => generateExercise(max, startSystem, targetSystem))}/>
      </div>

    </div>
  );

}
