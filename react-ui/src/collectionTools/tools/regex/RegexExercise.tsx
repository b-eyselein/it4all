import React, {useRef, useState} from 'react';
import {RegexExerciseContentFragment, useRegexCorrectionMutation} from '../../../graphql';
import {RegexCheatSheet} from './RegexCheatSheet';
import {BulmaTabs, Tabs} from '../../../helpers/BulmaTabs';
import {useTranslation} from 'react-i18next';
import {RegexCorrection} from './RegexCorrection';
import {StringSampleSolution} from '../StringSampleSolution';
import {Link} from 'react-router-dom';
import {ConcreteExerciseIProps} from '../../Exercise';
import {SampleSolutionTabContent} from '../../SampleSolutionTabContent';

type IProps = ConcreteExerciseIProps<RegexExerciseContentFragment>;

export function RegexExercise({exerciseFragment, contentFragment, showSampleSolutions, toggleSampleSolutions}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [showInfo, setShowInfo] = useState(false);
  const solutionInput = useRef<HTMLInputElement>(null);

  const [correctExercise, correctionMutationResult] = useRegexCorrectionMutation();

  if (!contentFragment.regexPart) {
    throw new Error('TODO');
  }

  const part = contentFragment.regexPart;

  function correct(): void {
    if (solutionInput.current) {
      correctExercise({
        variables: {
          collectionId: exerciseFragment.collectionId,
          exerciseId: exerciseFragment.exerciseId,
          part,
          solution: solutionInput.current.value
        }
      }).catch((error) => console.error(error));
    }
  }

  const correctionTabRender = () => <RegexCorrection mutationResult={correctionMutationResult}/>;
  correctionTabRender.displayName = 'RegexCorrectionTabRender';

  const sampleSolutionTabRender = () =>
    <SampleSolutionTabContent showSampleSolutions={showSampleSolutions} toggleSampleSolutions={toggleSampleSolutions}
                              renderSampleSolutions={() => contentFragment.regexSampleSolutions.map((sample, index) =>
                                <StringSampleSolution sample={sample} key={index}/>
                              )}/>;
  sampleSolutionTabRender.displayName = 'RegexSampleSolutionTabRender';

  const tabConfigs: Tabs = {
    correction: {name: t('correction'), render: correctionTabRender},
    sampleSolution: {name: t('sampleSolution_plural'), render: sampleSolutionTabRender}
  };

  return (
    <div className="container is-fluid">

      <div className="columns">
        <div className="column is-two-fifths-desktop">
          <h1 className="title is-3 has-text-centered">{t('exerciseText')}</h1>
          <div className="notification is-light-grey">{exerciseFragment.text}</div>

          <div className="field has-addons">
            <div className="control">
              <label className="button is-static" htmlFor="solution">{t('yourSolution')}:</label>
            </div>
            <div className="control is-expanded">
              <input type="text" className="input" id="solution" ref={solutionInput} placeholder="Ihre Lösung" autoFocus autoComplete="off"/>
            </div>
          </div>

          <div className="columns">
            <div className="column">
              <button className="button is-link is-fullwidth" onClick={correct}>{t('checkSolution')}</button>
            </div>
            <div className="column">
              <Link to={`./../../${exerciseFragment.exerciseId}`} className="button is-dark is-fullwidth">{t('endSolve')}</Link>
            </div>
          </div>
          <div className="buttons">
            <button className="button is-info is-fullwidth" onClick={() => setShowInfo((value) => !value)}>
              Hilfe {showInfo ? 'ausblenden' : 'anzeigen'}
            </button>
          </div>

          {showInfo && <RegexCheatSheet/>}
        </div>

        <div className="column">
          <BulmaTabs tabs={tabConfigs}/>
        </div>
      </div>
    </div>
  );
}
