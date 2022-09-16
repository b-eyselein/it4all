import {SqlExecutionResultFragment} from '../../../graphql';
import {SqlQueryResultTable} from './SqlQueryResultTable';
import {useTranslation} from 'react-i18next';

interface IProps {
  queryResult: SqlExecutionResultFragment;
}

export function SqlExecutionResultDisplay({queryResult}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <div className="mt-4">
      <h3 className="font-bold text-xl text-center">Vergleich der Ergebnistabellen</h3>

      <div className="grid grid-cols-2 gap-2">
        <div>
          <h4 className="font-bold text-center">Nutzer</h4>
          {queryResult.userResult
            ? <SqlQueryResultTable queryResult={queryResult.userResult}/>
            : <div className="notification is-danger has-text-centered">{t('errorExecutingLearnerSolution')}!</div>
          }
        </div>

        <div>
          <h4 className="font-bold text-center">Muster</h4>
          {queryResult.sampleResult
            ? <SqlQueryResultTable queryResult={queryResult.sampleResult}/>
            : <div className="notification is-warning">{t('errorExecutingSampleSolution')}</div>
          }
        </div>
      </div>
    </div>
  );
}
