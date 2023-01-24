import {Fragment, useState} from 'react';
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
      <h2 className="mb-2 font-bold text-center">Korrektur</h2>

      <div className="grid grid-cols-3">
        <div>
          <h3 className="font-bold text-center">Korrekte Klassen:</h3>
          <ul>
            {correctClasses.map((name) => <li key={name}>&#10004;&nbsp;<code className="has-text-dark-success">{name}</code></li>)}
          </ul>
        </div>

        <div>
          <h3 className="font-bold text-center">Fehlende Klassen:</h3>
          <ul>
            {missingClasses.map((name) => <li key={name}>&#10008;&nbsp;<code>{name}</code></li>)}
          </ul>
        </div>

        <div>
          <h3 className="font-bold text-center">Falsche Klassen</h3>
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
  const [state, setState] = useState<IState>({selectedClasses: []});

  const textParts = getUmlExerciseTextParts(exercise, content);
  const sampleClasses = content.umlSampleSolutions[0].classes.map(({name}) => name);

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

  const correct = (): void => setState(({selectedClasses}) => ({selectedClasses, correction: compareClasses(selectedClasses, sampleClasses)}));

  const isSelected = (className: string): boolean => state.selectedClasses.includes(className);

  return (
    <div className="container mx-auto">
      <h1 className="text-2xl font-bold text-center">{t('classSelection')}</h1>

      <div className="my-4 grid grid-cols-3 gap-2">
        <div className="col-span-2">
          <h2 className="font-bold text-center">{t('exerciseText')}</h2>

          {/* TODO: use component UmlExerciseText! */}
          <div className="notification is-light-grey">
            {textParts.map((textPart, index) =>
              <Fragment key={index}>
                {typeof textPart !== 'string'
                  ? (
                    <span onClick={() => toggleClass(textPart.className)} className={isSelected(textPart.className) ? 'text-blue-600' : 'text-slate-600'}>
                     {textPart.text}
                    </span>
                  )
                  : <span>{textPart}</span>}
              </Fragment>
            )}
          </div>
        </div>

        <div>
          <h2 className="font-bold text-center">{t('chosenClasses')}</h2>

          <ul className="list-disc list-inside">{state.selectedClasses.map((name) => <li key={name}>{name}</li>)}</ul>
        </div>
      </div>

      <div className="my-4 grid grid-cols-3 gap-2">
        <button className={classNames('p-2 rounded bg-blue-600 text-white w-full', {'opacity-50': state.correction})}
                onClick={correct} disabled={!!state.correction}>
          {t('correct')}
        </button>
        <Link className={classNames('block p-2 rounded text-white text-center w-full', state.correction ? 'bg-blue-600' : 'bg-black')}
              to={'../parts/diagramDrawingHelp'} /*disabled={!corrected}*/
              title="Führen Sie zuerst die Korrektur aus!">Zum nächsten Aufgabenteil
        </Link>
        <Link to="['../..']" className="p-2 rounded bg-black text-center text-white w-full">Bearbeiten beenden</Link>
      </div>

      {state.correction && <UmlClassSelectionCorrection correction={state.correction}/>}

    </div>
  );
}
