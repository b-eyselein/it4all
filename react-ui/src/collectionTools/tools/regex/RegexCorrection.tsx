import {RegexCorrectionMutationResult} from '../../../graphql';
import {WithQuery} from '../../../WithQuery';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {RegexExtractionResultDisplay} from './RegexExtractionResultDisplay';
import {RegexMatchingResultDisplay} from './RegexMatchingResultDisplay';
import {WithNullableNavigate} from '../../../WithNullableNavigate';

interface IProps {
  mutationResult: RegexCorrectionMutationResult;
}

export function RegexCorrection({mutationResult}: IProps): JSX.Element {

  // notCalledMessage={<></>}

  return (
    <WithQuery query={mutationResult}>
      {({regexExercise}) => <WithNullableNavigate t={regexExercise}>
        {({correct: {solutionSaved, result/* TODO:, resultSaved, proficienciesUpdated*/}}) => <>

          <SolutionSaved solutionSaved={solutionSaved}/>

          <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

          {result.__typename === 'RegexExtractionResult'
            ? <RegexExtractionResultDisplay result={result}/>
            : <RegexMatchingResultDisplay result={result}/>}

        </>}
      </WithNullableNavigate>}
    </WithQuery>
  );
}
