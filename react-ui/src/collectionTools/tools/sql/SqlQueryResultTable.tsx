import {SqlCell, SqlQueryResultFragment} from '../../../graphql';
import classNames from 'classnames';

interface IProps {
  queryResult: SqlQueryResultFragment;
}

const cellClasses = ['p-2', 'text-center', 'border', 'border-slate-200'];

export function SqlQueryResultTable({queryResult}: IProps): JSX.Element {

  return (
    <table className="w-full">
      <thead>
        <tr>
          {queryResult.columnNames.map((colName) => <th key={colName} className={classNames(cellClasses)}>{colName}</th>)}
        </tr>
      </thead>
      <tbody>
        {queryResult.rows.map((row, index) =>
          <tr key={index}>
            {queryResult.columnNames
              .map((colName) => row.cells.find(({key}) => key === colName)?.value)
              .filter<SqlCell>((cell): cell is SqlCell => !!cell)
              .map(({colName, content, different}) =>
                <td key={colName} className={classNames(cellClasses, {'bg-slate-200': different})}>
                  {content}
                </td>
              )}
          </tr>
        )}
      </tbody>
    </table>
  );
}
