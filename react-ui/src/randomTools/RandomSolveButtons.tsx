import {Link} from 'react-router-dom';
import {randomToolsUrlFragment} from '../urls';
import React from 'react';

interface RandomSolveButtonsIProps {
  toolId: string;
  correct: () => void;
  nextExercise: () => void;
}

export function RandomSolveButtons({toolId, correct, nextExercise}: RandomSolveButtonsIProps): JSX.Element {
  return (
    <div className="columns">
      <div className="column is-one-third-desktop">
        <button className="button is-link is-fullwidth" onClick={correct}>Lösung testen</button>
      </div>
      <div className="column is-one-third-desktop">
        <button className="button is-primary is-fullwidth" onClick={nextExercise}>Nächste Aufgabe</button>
      </div>
      <div className="column is-one-third-desktop">
        {/* FIXME: link target is not correct! */}
        <Link className="button is-dark is-fullwidth" to={`/${randomToolsUrlFragment}/${toolId}`}>Bearbeiten beenden</Link>
      </div>
    </div>
  );
}
