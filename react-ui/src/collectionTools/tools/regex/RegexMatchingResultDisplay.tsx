import React from "react";
import {BinaryClassificationResultType, RegexMatchingResultFragment, RegexMatchingSingleResultFragment} from "../../../generated/graphql";
import classNames from "classnames";

interface IProps {
  result: RegexMatchingResultFragment;
}

function RegexMatchingSingleResultDisplay({m: {resultType, matchData}}: { m: RegexMatchingSingleResultFragment }): JSX.Element {

  const correct = [BinaryClassificationResultType.TruePositive, BinaryClassificationResultType.TrueNegative]
    .includes(resultType);

  const wasMatched = [BinaryClassificationResultType.FalsePositive, BinaryClassificationResultType.TruePositive]
    .includes(resultType);

  return (
    <div className={classNames('notification', 'is-light-grey', correct ? 'has-text-dark-success' : 'has-text-danger')}>
      {correct ? <span>&#10004;</span> : <span>&#10008;</span>} &nbsp; <code>{matchData}</code> wurde {correct ? 'korrekt' : 'fälschlicherweise'}
      {wasMatched ? '' : <b> nicht</b>} erkannt.
    </div>
  );
}

export function RegexMatchingResultDisplay({result}: IProps): JSX.Element {
  return <>
    {result.matchingResults.map((matchingResult, index) =>
      <RegexMatchingSingleResultDisplay m={matchingResult} key={index}/>
    )}
  </>
}
