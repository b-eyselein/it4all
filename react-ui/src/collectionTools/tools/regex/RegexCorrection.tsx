import {RegexCorrectionMutationResult} from '../../../graphql';
import {WithQuery} from '../../../WithQuery';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {RegexMatchingResultDisplay} from './RegexMatchingResultDisplay';
import {WithNullableNavigate} from '../../../WithNullableNavigate';
import {RegexExtractionSingleResultDisplay} from './RegexExtractionResultDisplay';
import {MyFunComponent} from '../../../index';

interface IProps {
  mutationResult: RegexCorrectionMutationResult;
}

export const RegexCorrection: MyFunComponent<IProps> = ({mutationResult}: IProps) => (
  <WithQuery query={mutationResult}>
    {({regexExercise}) => <WithNullableNavigate t={regexExercise}>
      {({correct: {result/*, solutionId */}}) => <>

        <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

        {result.__typename === 'RegexMatchingResult'
          ? <RegexMatchingResultDisplay result={result}/>
          : <>{result.extractionResults.map((extractionResult, index) => <RegexExtractionSingleResultDisplay key={index} m={extractionResult}/>)}</>}

      </>}
    </WithNullableNavigate>}
  </WithQuery>
);
