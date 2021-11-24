import {SqlExecutionResultFragment} from '../../../graphql';
import {SqlQueryResultTable} from './SqlQueryResultTable';
import {useTranslation} from 'react-i18next';

interface IProps {
  queryResult: SqlExecutionResultFragment;
}

export function SqlExecutionResultDisplay({queryResult}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <>
      <h3 className="subtitle is-4 has-text-centered">Vergleich der Ergebnistabellen</h3>

      <div className="columns">

        <div className="column">
          <h4 className="subtitle is-4 has-text-centered">Nutzer</h4>
          {queryResult.userResult
            ? <div className="table-container">
              <SqlQueryResultTable queryResult={queryResult.userResult}/>
            </div>
            : <div className="notification is-danger has-text-centered">{t('errorExecutingLearnerSolution')}!</div>
          }
        </div>

        <div className="column">
          <h4 className="subtitle is-4 has-text-centered">Muster</h4>
          {queryResult.sampleResult
            ? <div className="table-container">
              <SqlQueryResultTable queryResult={queryResult.sampleResult}/>
            </div>
            : <div className="notification is-warning">{t('errorExecutingSampleSolution')}</div>
          }
        </div>
      </div>
    </>
  );
}
