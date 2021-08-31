import React, {useState} from 'react';
import {RegexExerciseContentFragment, RegexExPart, useRegexCorrectionMutation} from '../../../graphql';
import {RegexCheatSheet} from './RegexCheatSheet';
import {BulmaTabs, Tabs} from '../../../helpers/BulmaTabs';
import {useTranslation} from 'react-i18next';
import {RegexCorrection} from './RegexCorrection';
import {StringSampleSolution} from '../StringSampleSolution';
import {ConcreteExerciseIProps} from '../../Exercise';
import {SampleSolutionTabContent} from '../../SampleSolutionTabContent';
import {ChildLink, ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';
import {database} from '../../DexieTable';

type IProps = ConcreteExerciseIProps<RegexExerciseContentFragment, string>;

export function RegexExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [showInfo, setShowInfo] = useState(false);
  const [solution, setSolution] = useState(oldSolution ? oldSolution : '');

  const [correctExercise, correctionMutationResult] = useRegexCorrectionMutation();

  const correcting = correctionMutationResult.called && correctionMutationResult.loading;

  const part = RegexExPart.RegexSingleExPart;

  function correct(): void {
    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collectionId: exercise.collectionId, exerciseId: exercise.exerciseId, part, solution}})
      .then(() => setActiveTabId('correction'))
      .catch((error) => console.error(error));
  }

  const tabs: Tabs = {
    correction: {name: t('correction'), render: <RegexCorrection mutationResult={correctionMutationResult}/>},
    sampleSolution: {
      name: t('sampleSolution_plural'),
      render: <SampleSolutionTabContent>
        {() => content.regexSampleSolutions.map((sample, index) =>
          <StringSampleSolution sample={sample} key={index}/>
        )}
      </SampleSolutionTabContent>
    }
  };

  const [activeTabId, setActiveTabId] = useState<keyof Tabs>(Object.keys(tabs)[0]);

  const showHideHelpButton: ChildLink = {
    text: showInfo ? t('hideHelp') : t('showHelp'),
    to: () => setShowInfo((value) => !value),
    classNames: 'button is-info is-fullwidth'
  };

  return (
    <div className="container">

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

      <ExerciseControlButtons isCorrecting={correcting} correct={correct} endLink={`./../../${exercise.exerciseId}`} childLinks={[showHideHelpButton]}/>

      {showInfo && <RegexCheatSheet/>}

      <BulmaTabs tabs={tabs} activeTabId={activeTabId} setActiveTabId={setActiveTabId}/>
    </div>
  );
}
