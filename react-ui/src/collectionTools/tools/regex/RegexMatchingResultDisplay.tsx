import {BinaryClassificationResultType, RegexMatchingResultFragment, RegexMatchingSingleResultFragment} from '../../../graphql';
import classNames from 'classnames';
import {textColors} from '../../../consts';
import {MyFunComponent} from '../../../index';

interface IProps {
  result: RegexMatchingResultFragment;
}

function RegexMatchingSingleResultDisplay({m: {resultType, matchData}}: { m: RegexMatchingSingleResultFragment }): JSX.Element {

  const correct = [BinaryClassificationResultType.TruePositive, BinaryClassificationResultType.TrueNegative]
    .includes(resultType);

  const wasMatched = [BinaryClassificationResultType.FalsePositive, BinaryClassificationResultType.TruePositive]
    .includes(resultType);

  return (
    <div className={classNames('my-4', 'p-4', 'rounded', 'bg-gray-200', correct ? textColors.correct : textColors.inCorrect)}>
      {correct ? <span>&#10004;</span> : <span>&#10008;</span>} &nbsp; <code>{matchData}</code> wurde {correct ? 'korrekt' : 'fälschlicherweise'}
      {wasMatched ? '' : <b> nicht</b>} erkannt.
    </div>
  );
}

export const RegexMatchingResultDisplay: MyFunComponent<IProps> = ({result}: IProps) => (
  <>
    {result.matchingResults.map((matchingResult, index) =>
      <RegexMatchingSingleResultDisplay m={matchingResult} key={index}/>
    )}
  </>
);
