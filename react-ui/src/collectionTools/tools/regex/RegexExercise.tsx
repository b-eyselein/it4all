import {useState} from 'react';
import {RegexExerciseContentFragment, useRegexCorrectionMutation} from '../../../graphql';
import {RegexCheatSheet} from './RegexCheatSheet';
import {NewTabs} from '../../../helpers/BulmaTabs';
import {useTranslation} from 'react-i18next';
import {RegexCorrection} from './RegexCorrection';
import {StringSampleSolution} from '../StringSampleSolution';
import {ConcreteExerciseWithoutPartsProps} from '../../Exercise';
import {SampleSolutionTabContent} from '../../SampleSolutionTabContent';
import {ChildLink, ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';
import {database} from '../../DexieTable';
import update from 'immutability-helper';

type IProps = ConcreteExerciseWithoutPartsProps<RegexExerciseContentFragment, string>;

interface IState {
  showInfo: boolean;
  solution: string;
  activeTabId: string;
}

export function RegexExercise({exercise, content, oldSolution}: IProps): JSX.Element {

  const initialState: IState = {
    showInfo: false,
    solution: oldSolution || '',
    activeTabId: 'correction'
  };

  const {t} = useTranslation('common');
  const [state, setState] = useState(initialState);
  const [correctExercise, correctionMutationResult] = useRegexCorrectionMutation();

  function setActiveTabId(newTabId: string): void {
    setState((state) => update(state, {activeTabId: {$set: newTabId}}));
  }

  function setSolution(newSolution: string): void {
    setState((state) => update(state, {solution: {$set: newSolution}}));
  }

  const correcting = correctionMutationResult.called && correctionMutationResult.loading;

  function correct(): void {
    const solution = state.solution;

    database.upsertSolutionWithoutParts(exercise.toolId, exercise.collectionId, exercise.exerciseId, solution);

    correctExercise({variables: {collectionId: exercise.collectionId, exerciseId: exercise.exerciseId, solution}})
      .then(() => setActiveTabId('correction'))
      .catch((error) => console.error(error));
  }

  const showHideHelpButton: ChildLink = {
    text: state.showInfo ? t('hideHelp') : t('showHelp'),
    to: () => setState((state) => update(state, {$toggle: ['showInfo']})),
    otherClassNames: 'bg-cyan-400'
    // otherClassNames: 'button is-info is-fullwidth'
  };

  return (
    <div className="container mx-auto">

      <h1 className="mb-4 font-bold text-2xl text-center">{t('exerciseText')}</h1>
      <div className="my-4 p-4 rounded bg-gray-200">{exercise.text}</div>

      <div className="flex">
        <div className="p-2 rounded-l border-l border-y border-slate-500 bg-gray-100">{t('yourSolution')}:</div>
        <input type="text" defaultValue={state.solution} placeholder={t('yourSolution')} autoFocus autoComplete="off"
               onChange={(event) => setSolution(event.target.value)} className="p-2 rounded-r border border-slate-500 flex-grow"/>
      </div>

      <div className="my-4">
        <ExerciseControlButtons isCorrecting={correcting} correct={correct} endLink={`./../../${exercise.exerciseId}`} childLinks={[showHideHelpButton]}/>
      </div>

      {state.showInfo && <RegexCheatSheet/>}

      <NewTabs activeTabId={state.activeTabId} setActiveTabId={setActiveTabId}>
        {{
          correction: {name: t('correction'), render: <RegexCorrection mutationResult={correctionMutationResult}/>},
          sampleSolution: {
            name: t('sampleSolution_plural'),
            render: (
              <SampleSolutionTabContent>
                {() => content.regexSampleSolutions.map((sample, index) => <StringSampleSolution sample={sample} key={index}/>)}
              </SampleSolutionTabContent>
            )
          }
        }}
      </NewTabs>
    </div>
  );
}
