import {XmlDocumentResultFragment} from '../../../graphql';
import {bgColors, textColors} from '../../../consts';
import {useTranslation} from 'react-i18next';
import classNames from 'classnames';

interface IProps {
  result: XmlDocumentResultFragment;
}

export function XmlDocumentResultDisplay({result}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return result.errors.length === 0
    ? <div className={classNames('mt-2', 'p-2', 'rounded', bgColors.correct, 'text-white', 'text-center')}>{t('correctionSuccessful')}</div>
    : (
      <>
        {/*<PointsNotification points={0} maxPoints={0}/>*/}

        <p className={classNames('mt-2', 'mb-4', textColors.inCorrect)}>
          {t('correctionNotSuccessful')}. {t('ErrorsFound_{{count}}', {count: result.errors.length})}:
        </p>

        <ul className="list-disc list-inside">
          {result.errors.map(({errorType, errorMessage, line}, index) =>
            <li key={index} className={classNames('my-2', errorType === 'WARNING' ? 'has-text-dark-warning' : 'has-text-danger')}>
              <span className="font-bold">{t('errorInLine_{{line}}', {line})}</span>: <code>{errorMessage}</code>
            </li>
          )}
        </ul>
      </>
    );
}
