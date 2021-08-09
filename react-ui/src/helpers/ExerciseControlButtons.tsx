import React, {MouseEvent} from 'react';
import classNames from 'classnames';
import {Link} from 'react-router-dom';
import {useTranslation} from 'react-i18next';

export interface ChildLink {
  to: string | ((event: MouseEvent<HTMLButtonElement>) => void);
  text: string;
  classNames: string;
}

interface IProps {
  isCorrecting: boolean;
  correct: () => void;
  endLink: string;
  childLinks?: ChildLink[];
}

export function ExerciseControlButtons({isCorrecting, correct, endLink, childLinks}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <div className="columns my-3">
      <div className="column">
        <button className={classNames('button', 'is-link', 'is-fullwidth', {'is-loading': isCorrecting})} onClick={correct} disabled={isCorrecting}>
          {t('correct')}
        </button>
      </div>
      <div className="column">
        <Link to={endLink} className="button is-dark is-fullwidth">{t('endSolve')}</Link>
      </div>

      {childLinks && childLinks.map(({text, to, classNames}) => <div className="column" key={text}>
        {typeof to === 'function'
          ? <button className={classNames} onClick={to}>{text}</button>
          : <Link className={classNames} to={to}>{text}</Link>}
      </div>)}

    </div>
  );
}
