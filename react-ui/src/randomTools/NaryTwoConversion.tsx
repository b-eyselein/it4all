import React, {useState} from 'react';
import {NaryNumberReadOnlyInputComponent, NaryReadOnlyNumberInput} from './NaryNumberReadOnlyInput';
import {DECIMAL_SYSTEM, NaryIState} from './nary';
import {RandomSolveButtons} from './RandomSolveButtons';
import {useTranslation} from 'react-i18next';
import {randomInt} from './boolModel/bool-formula';
import {NaryLimits} from './NaryLimits';
import {NaryNumberInput} from './NaryNumberInput';

interface IState extends NaryIState {
  toConvertInput: NaryReadOnlyNumberInput;
  withIntermediateSteps: boolean;
  binaryAbsoluteString: string;
  binaryAbsoluteCorrect: boolean;
  invertedAbsoluteString: string;
  invertedAbsoluteCorrect: boolean;
}

function generateExercise(max = 128, withIntermediateSteps = false): IState {
  return {
    max,
    toConvertInput: {
      decimalNumber: randomInt(1, max), maxValueForDigits: max, numberingSystem: DECIMAL_SYSTEM
    },
    checked: false,
    withIntermediateSteps,
    binaryAbsoluteString: '',
    binaryAbsoluteCorrect: false,
    invertedAbsoluteString: '',
    invertedAbsoluteCorrect: false,
    solutionString: '',
    correct: false
  };
}


export function NaryTwoConversion(): JSX.Element {

  const {t} = useTranslation('common');
  const [state, setState] = useState<IState>(generateExercise);

  function correct(): void {
    const toConvert = state.toConvertInput.decimalNumber;

    const binaryAbsoluteCorrect = toConvert === parseInt(state.binaryAbsoluteString.replace(/\s+/, ''), 2);

    const invertedAbsoluteCorrect = toConvert === parseInt(state.invertedAbsoluteString
      .replace(/\s+/, '')
      .replace(/[01]/g, (x) => x === '0' ? '1' : '0'), 2);

    console.info(state.solutionString);

    const positiveSolution = parseInt(state.solutionString.replace(/\s+/, ''), 2);

    const awaitedBinaryAbsolute = toConvert.toString(2);
    const awaitedInvertedAbsolute = awaitedBinaryAbsolute
      .padStart(8, '0')
      .replace(/[01]/g, (x) => x === '0' ? '1' : '0');
    const awaitedPositiveSolution = parseInt(awaitedInvertedAbsolute, 2) + 1;

    const correct = positiveSolution === awaitedPositiveSolution;

    setState((state) => ({...state, checked: true, binaryAbsoluteCorrect, invertedAbsoluteCorrect, correct}));
  }

  return (
    <div className="container">

      <NaryLimits max={state.max} update={(newValue) => setState(generateExercise(newValue))}/>

      <div className="field">
        <div className="control has-text-centered">
          <label className="checkbox">
            <input type="checkbox" checked={state.withIntermediateSteps}
                   onChange={(event) => setState((state) => ({...state, withIntermediateSteps: event.target.checked}))}/> Mit Zwischenschritten
          </label>
        </div>
      </div>

      <p className="is-italic has-text-info has-text-centered">Geben Sie alle Binärzahlen mit einer Länge von 8 Bit ein!</p>

      <hr/>

      <NaryNumberReadOnlyInputComponent labelContent={t('startNumber')} naryNumberInput={state.toConvertInput}/>

      <br/>

      <NaryNumberInput labelContent={`Binärdarstellung von ${state.toConvertInput.decimalNumber}`} initialValue={state.binaryAbsoluteString}
                       checked={state.checked && state.withIntermediateSteps} correct={state.withIntermediateSteps} radix={2}
                       update={(newValue) => setState((state) => ({...state, binaryAbsoluteString: newValue}))} disabled={!state.withIntermediateSteps}/>

      <br/>

      <NaryNumberInput labelContent="Invertierung aller Bits" initialValue={state.invertedAbsoluteString} checked={state.checked && state.withIntermediateSteps}
                       correct={state.invertedAbsoluteCorrect} radix={2}
                       update={(newValue) => setState((state) => ({...state, invertedAbsoluteString: newValue}))} disabled={!state.withIntermediateSteps}/>

      <hr/>

      <NaryNumberInput labelContent={t('twoComplement')} initialValue={state.solutionString} checked={state.checked} correct={state.correct} radix={2}
                       update={(newValue) => setState((state) => ({...state, solutionString: newValue}))}/>

      <br/>

      <RandomSolveButtons toolId={'nary'} correct={correct} nextExercise={() => setState(({max, withIntermediateSteps}) => generateExercise(max, withIntermediateSteps))}/>

    </div>
  );
}
