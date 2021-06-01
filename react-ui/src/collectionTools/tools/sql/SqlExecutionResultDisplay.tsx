import React from "react";
import {SqlExecutionResultFragment} from "../../../generated/graphql";
import {SqlQueryResultTable} from "./SqlQueryResultTable";

interface IProps {
  queryResult: SqlExecutionResultFragment;
}

export function SqlExecutionResultDisplay({queryResult}: IProps): JSX.Element {
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
            : <div className=" notification is-danger">Es gab einen Fehler beim Ausführen ihrer Query!</div>
          }
        </div>

        <div className="column">
          <h4 className="subtitle is-4 has-text-centered">Muster</h4>
          <div className="table-container">
            <SqlQueryResultTable queryResult={queryResult.sampleResult!!}/>
          </div>
        </div>
      </div>
    </>
  );
}
