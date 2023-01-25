import React, {Fragment, useState} from 'react';
import {ExerciseSolveFieldsFragment, UmlExerciseContentFragment} from '../../../graphql';
import {getUmlExerciseTextParts} from './uml-helpers';
import classNames from 'classnames';
import {Link} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import {UmlClassSelectionCorrection} from './UmlClassSelectionCorrection';

interface IProps {
  exercise: ExerciseSolveFieldsFragment;
  content: UmlExerciseContentFragment;
}

export interface UmlClassSelectionCorrectionResult {
  correctClasses: string[];
  missingClasses: string[];
  wrongClasses: string[];
}

interface IState {
  selectedClasses: string[];
  correction?: UmlClassSelectionCorrectionResult;
}

function compareClasses(userClasses: string[], sampleClasses: string[]): UmlClassSelectionCorrectionResult {
  return {
    correctClasses: userClasses.filter((name) => sampleClasses.includes(name)),
    missingClasses: sampleClasses.filter((name) => !userClasses.includes(name)),
    wrongClasses: userClasses.filter((name) => !sampleClasses.includes(name))
  };
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

      <p className="my-4 text-xl text-center">Wählen Sie die Klassen, die sie modellieren würden, durch Anklicken im Text aus.</p>

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

      {state.correction && <UmlClassSelectionCorrection {...state.correction}/>}

    </div>
  );
}
