import React, {Fragment, useState} from 'react';
import {ExerciseSolveFieldsFragment, UmlExerciseContentFragment} from '../../../graphql';
import {getUmlExerciseTextParts} from './uml-helpers';
import classNames from 'classnames';
import {Link} from 'react-router-dom';
import {useTranslation} from 'react-i18next';

interface IProps {
  exercise: ExerciseSolveFieldsFragment;
  content: UmlExerciseContentFragment;
}

interface Correction {
  correctClasses: string[];
  missingClasses: string[];
  wrongClasses: string[];
}

interface IState {
  selectedClasses: string[];
  correction?: Correction;
}

function compareClasses(userClasses: string[], sampleClasses: string[]): Correction {
  return {
    correctClasses: userClasses.filter((name) => sampleClasses.includes(name)),
    missingClasses: sampleClasses.filter((name) => !userClasses.includes(name)),
    wrongClasses: userClasses.filter((name) => !sampleClasses.includes(name))
  };
}

function UmlClassSelectionCorrection({correction}: { correction: Correction }): JSX.Element {

  const {correctClasses, missingClasses, wrongClasses} = correction;

  return (
    <>
      <hr/>

      <h2 className="subtitle is-3 has-text-centered">Korrektur</h2>

      <div className="columns">
        <div className="column">
          <h3 className="subtitle is-4 has-text-centered">Korrekte Klassen:</h3>
          <ul>
            {correctClasses.map((name) => <li key={name}>&#10004;&nbsp;<code className="has-text-dark-success">{name}</code></li>)}
          </ul>
        </div>

        <div className="column">
          <h3 className="subtitle is-4 has-text-centered">Fehlende Klassen:</h3>
          <ul>
            {missingClasses.map((name) => <li key={name}>&#10008;&nbsp;<code>{name}</code></li>)}
          </ul>
        </div>

        <div className="column">
          <h3 className="subtitle is-4 has-text-centered">Falsche Klassen</h3>
          <ul>
            {wrongClasses.map((name) => <li key={name}>&#10067;&nbsp;<code>{name}</code></li>)}
          </ul>
        </div>
      </div>
    </>
  );
}

export function UmlClassSelection({exercise, content}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const textParts = getUmlExerciseTextParts(exercise, content);

  const sampleClasses = content.umlSampleSolutions[0].classes.map(({name}) => name);

  const [state, setState] = useState<IState>({selectedClasses: []});

  function toggleClass(name: string): void {
    setState(({selectedClasses, correction}) => {
        const newSelectedClasses = selectedClasses.includes(name)
          ? selectedClasses.filter((n) => n !== name)
          : [...selectedClasses, name].sort();

        const newCorrection = correction
          ? compareClasses(newSelectedClasses, sampleClasses)
          : undefined;

        return {selectedClasses: newSelectedClasses, correction: newCorrection};
      }
    );
  }

  function correct(): void {
    setState(({selectedClasses}) => ({selectedClasses, correction: compareClasses(selectedClasses, sampleClasses)}));
  }

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('classSelection')}</h1>

      <div className="columns">
        <div className="column is-two-thirds-desktop">
          <h2 className="subtitle is-3 has-text-centered">{t('exerciseText')}</h2>

          {/* TODO: use component UmlExerciseText! */}
          <div className="notification is-light-grey">
            {textParts.map((textPart, index) =>
              <Fragment key={index}>
                {typeof textPart !== 'string'
                  ? <span onClick={() => toggleClass(textPart.className)}
                          className={state.selectedClasses.includes(textPart.className) ? 'has-text-link' : 'has-text-black'}>{textPart.text}</span>
                  : <span>{textPart}</span>}
              </Fragment>
            )}
          </div>
        </div>

        <div className="column is-one-third-desktop">
          <h2 className="subtitle is-3 has-text-centered">{t('chosenClasses')}</h2>

          <div className="content">
            <ul>{state.selectedClasses.map((name) => <li key={name}>{name}</li>)}</ul>
          </div>
        </div>
      </div>

      <div className="columns">
        <div className="column">
          <button className={classNames('button', 'is-fullwidth', state.correction ? 'is-dark' : 'is-link')} onClick={correct} disabled={!!state.correction}>
            {t('correct')}
          </button>
        </div>
        <div className="column">
          <Link className={classNames('button', 'is-fullwidth', state.correction ? 'is-link' : 'is-dark')}
                to={'../parts/diagramDrawingHelp'} /*disabled={!corrected}*/
                title="Führen Sie zuerst die Korrektur aus!">Zum nächsten Aufgabenteil
          </Link>
        </div>
        <div className="column">
          <Link to="['../..']" className="button is-dark-warning is-fullwidth">Bearbeiten beenden</Link>
        </div>
      </div>

      {state.correction && <UmlClassSelectionCorrection correction={state.correction}/>}

    </div>
  );
}
