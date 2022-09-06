import {FilesSolutionInput, FlaskExerciseContentFragment, useFlaskCorrectionMutation} from '../../../graphql';
import {ConcreteExerciseWithoutPartsProps} from '../../Exercise';
import {FilesExercise} from '../FilesExercise';
import {WithQuery} from '../../../WithQuery';
import {PointsNotification} from '../../../helpers/PointsNotification';
import classNames from 'classnames';
import {database} from '../../DexieTable';
import {WithNullableNavigate} from '../../../WithNullableNavigate';
import {IExerciseFile} from '../../exerciseFile';
import {FlaskExerciseDescription} from './FlaskExerciseDescription';

type IProps = ConcreteExerciseWithoutPartsProps<FlaskExerciseContentFragment, FilesSolutionInput>;

export function FlaskExercise({exercise, content, oldSolution}: IProps): JSX.Element {

  const [correctExercise, correctionMutationResult] = useFlaskCorrectionMutation();

  const exerciseDescription = <FlaskExerciseDescription exercise={exercise} content={content}/>;

  function correct(files: IExerciseFile[], onCorrect: () => void): void {
    const solution: FilesSolutionInput = {
      files: files.map(({name, content, editable}) => ({name, content, editable}))
    };

    database.upsertSolutionWithoutParts(exercise.toolId, exercise.collectionId, exercise.exerciseId, solution);

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution}})
      .then(onCorrect)
      .catch((err) => console.error(err));
  }

  const correctionTabRender = (
    <WithQuery query={correctionMutationResult}>
      {({flaskExercise}) => <WithNullableNavigate t={flaskExercise}>
        {({correct: {result/*, solutionId, proficienciesUpdated*/}}) => <>
          {/*<SolutionSaved solutionSaved={solutionSaved}/>*/}

          <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

          {result.testResults.map((testResult, index) =>
            <div className="my-3" key={index}>
              <div className={classNames('message', testResult.successful ? 'is-success' : 'is-danger')}>
                <header className="message-header">{testResult.testName}</header>
                <div className="message-body">
                  <pre>{testResult.stderr.join('\n')}</pre>
                </div>
              </div>
            </div>
          )}
        </>}
      </WithNullableNavigate>}
    </WithQuery>
  );

  return (
    <FilesExercise
      exerciseDescription={exerciseDescription}
      defaultFiles={content.files}
      oldSolution={oldSolution}
      sampleSolutions={content.flaskSampleSolutions}
      correct={correct}
      correctionTabRender={correctionTabRender}
      isCorrecting={correctionMutationResult.called && correctionMutationResult.loading}/>
  );
}
