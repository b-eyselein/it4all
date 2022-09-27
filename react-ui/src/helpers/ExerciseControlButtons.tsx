import {MouseEvent} from 'react';
import classNames from 'classnames';
import {Link} from 'react-router-dom';
import {useTranslation} from 'react-i18next';

export interface ChildLink {
  to: string | ((event: MouseEvent<HTMLButtonElement>) => void);
  text: string;
  otherClassNames: string;
}

interface IProps {
  isCorrecting: boolean;
  correct: () => void;
  endLink: string;
  childLinks?: ChildLink[];
}

export function ExerciseControlButtons({isCorrecting, correct, endLink, childLinks = []}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <div className="flex flex-row my-3">

      <button type="button" className="p-2 flex-grow rounded bg-blue-500 text-white disabled:opacity-25" onClick={correct} disabled={isCorrecting}>
        {t('correct')}
      </button>

      <Link to={endLink} className="ml-2 p-2 flex-grow rounded bg-gray-800 text-white text-center">{t('endSolve')}</Link>

      {childLinks.map(({text, to, otherClassNames}) => <div className="column" key={text}>
        {typeof to === 'function'
          ? <button className={classNames('flex-grow', otherClassNames)} onClick={to}>{text}</button>
          : <Link className={classNames('flex-grow', otherClassNames)} to={to}>{text}</Link>}
      </div>)}

    </div>
  );
}
