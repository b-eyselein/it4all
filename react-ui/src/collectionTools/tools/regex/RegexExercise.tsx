import React, {useState} from "react";
import {ExerciseSolveFieldsFragment, RegexExerciseContentFragment} from "../../../generated/graphql";
import {RegexCheatSheet} from './RegexCheatSheet';
import {BulmaTabs, TabConfig} from "../../../helpers/BulmaTabs";
import {useTranslation} from "react-i18next";
import {RegexCorrection} from './RegexCorrection';
import {StringSampleSolution} from "../StringSampleSolution";

interface IProps {
  exerciseFragment: ExerciseSolveFieldsFragment;
  contentFragment: RegexExerciseContentFragment;
}

export function RegexExercise({exerciseFragment, contentFragment}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [showInfo, setShowInfo] = useState(false);
  const [showSampleSolutions, setShowSampleSolutions] = useState(false);

  function correct(): void {
    console.info('TODO'!);
  }

  const tabConfigs: TabConfig[] = [
    {id: 'correction', name: t('correction'), render: () => <RegexCorrection/>},
    {
      id: 'sampleSolutions', name: t('sampleSolution_plural'), render: () => <div>
        <div className="buttons">
          <button className="button is-primary is-fullwidth" onClick={() => setShowSampleSolutions((value) => !value)}>
            Musterlösungen {showSampleSolutions ? 'ausblenden' : 'anzeigen'}
          </button>
        </div>

        {showSampleSolutions && contentFragment.regexSampleSolutions.map((sample, index) => <StringSampleSolution sample={sample} key={index}/>)}
      </div>
    }
  ];

  return (
    <div className="container is-fluid">

      <div className="columns">
        <div className="column is-two-fifths-desktop">
          <h1 className="title is-3 has-text-centered">{t('exerciseText')}</h1>
          <div className="notification is-light-grey">{exerciseFragment.text}</div>

          <div className="field has-addons">
            <div className="control">
              <label className="button is-static" htmlFor="solution">Ihre Lösung:</label>
            </div>
            <div className="control is-expanded">
              {/* [(ngModel)]="solution" */}
              <input type="text" className="input" id="solution" placeholder="Ihre Lösung" autoFocus autoComplete="off"/>
            </div>
          </div>

          <div className="columns">
            <div className="column">
              <button className="button is-link is-fullwidth" onClick={correct}>Lösung testen</button>
            </div>
            <div className="column">
              {/* [routerLink]="['../..']" */}
              <a className="button is-dark is-fullwidth">Bearbeiten beenden</a>
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
          {/*    <it4all-tabs>

        <it4all-tab [title]="sampleSolutionsTabTitle">

        </it4all-tab>
      </it4all-tabs>
      */}
        </div>
      </div>
    </div>


  )

}
