import classNames from 'classnames';

export function SolutionSaved({solutionSaved}: { solutionSaved: boolean }): JSX.Element {
  return (
    <div className={classNames('notification', 'is-light-grey', solutionSaved ? 'has-text-dark-success' : 'has-text-danger')}>
      &#10004; Ihre Lösung wurde {solutionSaved ? '' : <b>nicht</b>} gespeichert.
    </div>
  );
}
