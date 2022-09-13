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
    <div className="my-4 grid grid-cols-3 gap-4">
      <button className="p-2 rounded bg-blue-500 text-white w-full" onClick={correct}>{t('correctSolution')}</button>

      <button className="p-2 rounded bg-cyan-400 text-white w-full" onClick={nextExercise}>{t('nextExercise')}</button>

      <Link className="p-2 rounded bg-slate-600 text-white w-full text-center" to={`/${randomToolsUrlFragment}/${toolId}`}>{t('endSolve')}</Link>
    </div>
  );
}
