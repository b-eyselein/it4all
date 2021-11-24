import {Link} from 'react-router-dom';
import {randomToolsUrlFragment} from '../urls';
import {useTranslation} from 'react-i18next';

interface RandomSolveButtonsIProps {
  toolId: string;
  correct: () => void;
  nextExercise: () => void;
}

export function RandomSolveButtons({toolId, correct, nextExercise}: RandomSolveButtonsIProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <div className="columns">
      <div className="column is-one-third-desktop">
        <button className="button is-link is-fullwidth" onClick={correct}>{t('correctSolution')}</button>
      </div>
      <div className="column is-one-third-desktop">
        <button className="button is-primary is-fullwidth" onClick={nextExercise}>{t('nextExercise')}</button>
      </div>
      <div className="column is-one-third-desktop">
        <Link className="button is-dark is-fullwidth" to={`/${randomToolsUrlFragment}/${toolId}`}>{t('endSolve')}</Link>
      </div>
    </div>
  );
}
