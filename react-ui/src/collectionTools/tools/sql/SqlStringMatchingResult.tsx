import React from "react";
import {MatchType, StringMatchingResultFragment} from "../../../generated/graphql";
import classNames from "classnames";

interface IProps {
  matchName: string;
  matchSingularName: string;
  matchingResult: StringMatchingResultFragment;
}

export function getCssClassForMatchType(matchType: MatchType): string {
  return (matchType === MatchType.SuccessfulMatch) ? 'has-text-success' : 'has-text-danger';
}

export function getTextForMatchType(matchType: MatchType): string {
  return matchType === MatchType.SuccessfulMatch ? 'ist korrekt' : 'ist nicht komplett richtig.';
}


export function SqlStringMatchingResult({matchName, matchSingularName, matchingResult}: IProps): JSX.Element {

  const {allMatches, notMatchedForUser, notMatchedForSample} = matchingResult;

  const successful = allMatches.every((m) => m.matchType === MatchType.SuccessfulMatch)
    && notMatchedForUser.length === 0
    && notMatchedForSample.length === 0;

  return (
    <>
      <div className={classNames(successful ? 'has-text-success' : 'has-text-danger')}>
        {/* ({matchingResult.points.toFixed(1)} / {matchingResult.maxPoints.toFixed(1)} P) */}
        Der Vergleich der {matchName} war {successful ? "" : "nicht"} erfolgreich.
      </div>

      {!successful && <div className="content">
        <ul>
          {allMatches.map((match, index) =>
            <li className={getCssClassForMatchType(match.matchType)} key={index}>
              Die Angabe {{matchSingularName}}
              <code>{match.userArg}</code> {getTextForMatchType(match.matchType)}.
            </li>
          )}
          {notMatchedForUser.map((notMatchedForUser, index) =>
            <li className="has-text-danger" key={index}>
              Die Angabe {matchSingularName} <code>{notMatchedForUser}</code> ist falsch.
            </li>
          )}
          {notMatchedForSample.map((notMatchedForSample, index) =>
            <li className="has-text-danger" key={index}>
              Die Angabe {matchSingularName} <code>{notMatchedForSample}</code> fehlt.
            </li>
          )}
        </ul>
      </div>
      }
    </>
  );

}
