import classNames from 'classnames';

interface IProps {
  points: number;
  maxPoints: number;
}

function getTextColorForPercentage(percentage: number): string {
  if (percentage >= 90) {
    return 'has-text-dark-success';
  } else if (percentage >= 75) {
    return 'has-text-warning';
  } else {
    return 'has-text-danger';
  }
}

export function PointsNotification({points, maxPoints}: IProps): JSX.Element {

  const percentage = points / maxPoints * 100;

  const textColor = getTextColorForPercentage(percentage);

  return (
    <div className={classNames('notification', 'is-light-grey', textColor)}>
      <p className="has-text-centered">
        Sie haben {points} von maximal {maxPoints} Punkten erreicht.
      </p>

      <br/>

      <progress className="progress" value={percentage} max="100">{percentage}%</progress>
    </div>
  );

}
