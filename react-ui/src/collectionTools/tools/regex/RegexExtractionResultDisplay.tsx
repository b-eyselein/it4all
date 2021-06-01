import React from "react";
import {MatchType, RegexExtractionMatchFragment, RegexExtractionResultFragment, RegexExtractionSingleResultFragment} from "../../../generated/graphql";
import classNames from "classnames";

interface IProps {
  result: RegexExtractionResultFragment;
}

function RegexExtractionSingleResultDisplay({m: {extractionMatchingResult, base}}: { m: RegexExtractionSingleResultFragment }): JSX.Element {

  function isCorrect(m: RegexExtractionMatchFragment): boolean {
    return m.matchType === MatchType.SuccessfulMatch;
  }

  return (
    <div className="notification">
      <p className="has-text-centered">Suche in: <code>{base}</code></p>

      <div className="columns is-multiline my-3">
        {extractionMatchingResult.allMatches.map((m, index) =>
          <div className="column is-half-desktop" key={index}>

            <div className={classNames('notification', isCorrect(m) ? 'is-success' : 'is-danger')}>
              {isCorrect(m) ? '&#10004;' : '&#10008;'} Erwartet: <code>{m.sampleArg}</code>, bekommen: <code>{m.userArg}</code>
            </div>

          </div>
        )}

        {extractionMatchingResult.notMatchedForUser.map((mu, index) =>
          <div className="column is-half-desktop" key={index}>
            <div className="notification is-danger">
              <code>{mu}</code> sollte nicht gefunden werden!
            </div>
          </div>
        )}

        {extractionMatchingResult.notMatchedForSample.map((ms, index) =>
          <div className="column is-half-desktop" key={index}>
            <div className="notification is-danger">
              <code>{ms}</code> sollte gefunden werden!
            </div>
          </div>
        )}

      </div>
    </div>
  )
}

export function RegexExtractionResultDisplay({result}: IProps): JSX.Element {
  return (
    <>
      {result.extractionResults.map((extractionResult, index) =>
        <RegexExtractionSingleResultDisplay m={extractionResult} key={index}/>
      )}
    </>
  );
}
