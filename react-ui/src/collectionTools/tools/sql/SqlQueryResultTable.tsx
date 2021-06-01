import React from "react";
import {SqlQueryResultFragment} from "../../../generated/graphql";
import classNames from "classnames";

interface IProps {
  queryResult: SqlQueryResultFragment;
}

export function SqlQueryResultTable({queryResult}: IProps): JSX.Element {

  return (
    <table className="table is-bordered is-fullwidth">
      <thead>
        <tr>
          {queryResult.columnNames.map((colName) => <th key={colName}>{colName}</th>)}
        </tr>
      </thead>
      <tbody>
        {queryResult.rows.map((row, index) => <tr key={index}>
            {queryResult.columnNames
              .map((colName) => row.cells.find((r) => r.key === colName)!!.value)
              .map(({colName, content, different}) => <td key={colName} className={classNames({'is-light-danger': different})}>{content}</td>
              )}
          </tr>
        )}
      </tbody>
    </table>
  )
}
