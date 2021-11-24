import { useState } from 'react';
import {SqlQueryResultFragment} from '../../../graphql';
import classNames from 'classnames';
import {SqlQueryResultTable} from './SqlQueryResultTable';

interface IProps {
  tables: SqlQueryResultFragment[];
}

export function SqlTableContents({tables}: IProps): JSX.Element {

  const [shownDbContent, setShownDbContent] = useState<SqlQueryResultFragment | undefined>();

  function activateModal(dbContent: SqlQueryResultFragment): void {
    setShownDbContent((currentShownDbContent) =>
      currentShownDbContent?.tableName === dbContent.tableName ? undefined : dbContent
    );
  }

  return (
    <>
      <div className="columns is-multiline">
        {tables.map((dbContent, index) =>
          <div className="column is-one-quarter-desktop" key={index}>
            <button className={classNames('button', 'is-fullwidth', {'is-info': dbContent === shownDbContent})} onClick={() => activateModal(dbContent)}>
              {dbContent.tableName}
            </button>
          </div>
        )}

      </div>

      {shownDbContent && <div className="card">
        <header className="card-header">
          <p className="card-header-title">{shownDbContent.tableName}</p>
        </header>
        <section className="card-content">
          <div className=" table-container">
            <SqlQueryResultTable queryResult={shownDbContent}/>
          </div>
        </section>
      </div>}
    </>
  );
}
