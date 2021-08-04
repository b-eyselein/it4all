import React, {useState} from 'react';
import {RegexExerciseContentFragment, useRegexCorrectionMutation} from '../../../graphql';
import {RegexCheatSheet} from './RegexCheatSheet';
import {BulmaTabs, Tabs} from '../../../helpers/BulmaTabs';
import {useTranslation} from 'react-i18next';
import {RegexCorrection} from './RegexCorrection';
import {StringSampleSolution} from '../StringSampleSolution';
import {ConcreteExerciseIProps} from '../../Exercise';
import {SampleSolutionTabContent} from '../../SampleSolutionTabContent';
import {ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';
import {database} from '../../DexieTable';

type IProps = ConcreteExerciseIProps<RegexExerciseContentFragment, string>;

export function RegexExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [showInfo, setShowInfo] = useState(false);
  const [solution, setSolution] = useState(oldSolution ? oldSolution : '');

  const [correctExercise, correctionMutationResult] = useRegexCorrectionMutation();

  const correcting = correctionMutationResult.called && correctionMutationResult.loading;

  if (!content.regexPart) {
    throw new Error('TODO');
  }

  const part = content.regexPart;

  function correct(): void {
    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collectionId: exercise.collectionId, exerciseId: exercise.exerciseId, part, solution}})
      .then(() => setActiveTabId('correction'))
      .catch((error) => console.error(error));
  }

  const correctionTabRender = () => <RegexCorrection mutationResult={correctionMutationResult}/>;
  correctionTabRender.displayName = 'RegexCorrectionTabRender';

  const sampleSolutionTabRender = () => <SampleSolutionTabContent>
    {() => content.regexSampleSolutions.map((sample, index) =>
      <StringSampleSolution sample={sample} key={index}/>
    )}
  </SampleSolutionTabContent>;
  sampleSolutionTabRender.displayName = 'RegexSampleSolutionTabRender';

  const tabs: Tabs = {
    correction: {name: t('correction'), render: correctionTabRender},
    sampleSolution: {name: t('sampleSolution_plural'), render: sampleSolutionTabRender}
  };
  const [activeTabId, setActiveTabId] = useState<keyof Tabs>(Object.keys(tabs)[0]);

  return (
    <div className="container is-fluid">

      <div className="columns">
        <div className="column is-two-fifths-desktop">
          <h1 className="title is-3 has-text-centered">{t('exerciseText')}</h1>
          <div className="notification is-light-grey">{exercise.text}</div>

          <div className="field has-addons">
            <div className="control">
              <button className="button is-static">{t('yourSolution')}:</button>
            </div>
            <div className="control is-expanded">
              <input type="text" className="input" defaultValue={solution} onChange={(event) => setSolution(event.target.value)}
                     placeholder={t('yourSolution')} autoFocus autoComplete="off"/>
            </div>
          </div>

          <ExerciseControlButtons isCorrecting={correcting} correct={correct} endLink={`./../../${exercise.exerciseId}`}>
            <div className="column">
              <button className="button is-info is-fullwidth" onClick={() => setShowInfo((value) => !value)}>
                {showInfo ? t('hideHelp') : t('hideHelp')}
              </button>
            </div>
          </ExerciseControlButtons>
          {/*
          <div className="columns">
            <div className="column">
              <button className="button is-link is-fullwidth" onClick={correct}>{t('checkSolution')}</button>
            </div>
            <div className="column">
              <Link to={`./../../${exercise.exerciseId}`} className="button is-dark is-fullwidth">{t('endSolve')}</Link>
            </div>
          </div>
          <div className="buttons">
            <button className="button is-info is-fullwidth" onClick={() => setShowInfo((value) => !value)}>
              Hilfe {showInfo ? 'ausblenden' : 'anzeigen'}
            </button>

        </div>
            */}

          {showInfo && <RegexCheatSheet/>}
        </div>

        <div className="column">
          <BulmaTabs tabs={tabs} activeTabId={activeTabId} setActiveTabId={setActiveTabId}/>
        </div>
      </div>
    </div>
  );
}
