import {useState} from 'react';
import {SqlQueryResultFragment} from '../../../graphql';
import classNames from 'classnames';
import {SqlQueryResultTable} from './SqlQueryResultTable';
import {NewCard} from '../../../helpers/BulmaCard';

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
      <div className="my-4 grid grid-cols-4 gap-2">
        {tables.map((dbContent, index) =>
          <button key={index} onClick={() => activateModal(dbContent)}
                  className={classNames('p-2', 'rounded', 'w-full', dbContent === shownDbContent ? ['bg-blue-500', 'text-white'] : ['border', 'border-slate-500',])}>
            {dbContent.tableName}
          </button>
        )}
      </div>

      {shownDbContent && <NewCard title={shownDbContent.tableName}>
        <SqlQueryResultTable queryResult={shownDbContent}/>
      </NewCard>}
    </>
  );
}
