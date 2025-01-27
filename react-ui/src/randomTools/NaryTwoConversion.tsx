import {useState} from 'react';
import {NaryIState} from './nary';
import {RandomSolveButtons} from './RandomSolveButtons';
import {useTranslation} from 'react-i18next';
import {randomInt} from './boolModel/booleanFormulaGenerator';
import {NaryLimits} from './NaryLimits';
import {NaryNumberInput} from './NaryNumberInput';

interface IState extends NaryIState {
  toConvert: number;
  withIntermediateSteps: boolean;
  binaryAbsoluteString: string;
  binaryAbsoluteCorrect: boolean;
  invertedAbsoluteString: string;
  invertedAbsoluteCorrect: boolean;
}

function generateExercise(max = 128, withIntermediateSteps = false): IState {
  return {
    max,
    toConvert: randomInt(1, max),
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
    const toConvert = state.toConvert;

    const binaryAbsoluteCorrect = toConvert === parseInt(state.binaryAbsoluteString.replace(/\s+/, ''), 2);

    const invertedAbsoluteCorrect = toConvert === parseInt(state.invertedAbsoluteString
      .replace(/\s+/, '')
      .replace(/[01]/g, (x) => x === '0' ? '1' : '0'), 2);

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
    <div className="container mx-auto">
      <h1 className="mb-4 font-bold text-2xl text-center">{t('naryTwoConversion')}</h1>

      <NaryLimits max={state.max} update={(newValue) => setState(generateExercise(newValue))}/>

      <div className="my-4 text-center">
        <label htmlFor="withSubSteps">
          <input type="checkbox" id="withSubSteps" checked={state.withIntermediateSteps}
                 onChange={(event) => setState((state) => ({...state, withIntermediateSteps: event.target.checked}))}/> Mit Zwischenschritten
        </label>
      </div>

      <p className="italic text-cyan-600 text-center">Geben Sie alle Binärzahlen mit einer Länge von 8 Bit ein!</p>

      <hr className="my-4"/>

      <h2 className="text-2xl text-center">Bilden Sie das Zweierkomplement der Zahl -{state.toConvert}<sub>10</sub>!</h2>

      <NaryNumberInput labelContent={`Binärdarstellung von ${state.toConvert}`} initialValue={state.binaryAbsoluteString}
                       checked={state.checked && state.withIntermediateSteps} correct={state.withIntermediateSteps} radix={2}
                       update={(newValue) => setState((state) => ({...state, binaryAbsoluteString: newValue}))} disabled={!state.withIntermediateSteps}/>

      <NaryNumberInput labelContent="Invertierung aller Bits" initialValue={state.invertedAbsoluteString} checked={state.checked && state.withIntermediateSteps}
                       correct={state.invertedAbsoluteCorrect} radix={2}
                       update={(newValue) => setState((state) => ({...state, invertedAbsoluteString: newValue}))} disabled={!state.withIntermediateSteps}/>

      <hr className="my-4"/>

      <NaryNumberInput labelContent={t('twoComplement')} initialValue={state.solutionString} checked={state.checked} correct={state.correct} radix={2}
                       update={(newValue) => setState((state) => ({...state, solutionString: newValue}))}/>

      <RandomSolveButtons toolId={'nary'} correct={correct}
                          nextExercise={() => setState(({max, withIntermediateSteps}) => generateExercise(max, withIntermediateSteps))}/>

    </div>
  );
}
