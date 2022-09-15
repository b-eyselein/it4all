import classNames from 'classnames';
import {useTranslation} from 'react-i18next';
import {textColors} from '../consts';

interface IProps {
  points: number;
  maxPoints: number;
}

function getTextColorForPercentage(percentage: number): string {
  if (percentage >= 90) {
    return textColors.correct;
  } else if (percentage >= 75) {
    return textColors.partlyCorrect;
  } else {
    return textColors.inCorrect;
  }
}

export function PointsNotification({points, maxPoints}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const percentage = points / maxPoints * 100;
  const textColor = getTextColorForPercentage(percentage);

  return (
    <div className={classNames('my-2', 'p-2', 'rounded', 'bg-slate-200', textColor)}>
      <div className="mb-2 text-center">{t('reached{{points}}PointsOf{{maxPoints}}Points', {points, maxPoints})}</div>

      <progress className="bg-white w-full" value={percentage} max="100">{percentage}%</progress>
    </div>
  );

}
