import React from 'react';
import classNames from 'classnames';
import {Link} from 'react-router-dom';
import {useTranslation} from 'react-i18next';

interface IProps {
  isCorrecting: boolean;
  correct: () => void;
  endLink: string;
  children?: JSX.Element;
}


export function ExerciseControlButtons({isCorrecting, correct, endLink, children}: IProps): JSX.Element {

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

      {children && children}

    </div>
  );
}
