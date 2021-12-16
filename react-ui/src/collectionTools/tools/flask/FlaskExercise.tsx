import {ExerciseFileFragment, FilesSolutionInput, FlaskExerciseContentFragment, FlaskExercisePart, useFlaskCorrectionMutation} from '../../../graphql';
import {ConcreteExerciseIProps} from '../../Exercise';
import {FilesExercise, updateFileContents} from '../FilesExercise';
import {WithQuery} from '../../../WithQuery';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {PointsNotification} from '../../../helpers/PointsNotification';
import classNames from 'classnames';
import {database} from '../../DexieTable';
import {WithNullableNavigate} from '../../../WithNullableNavigate';

type IProps = ConcreteExerciseIProps<FlaskExerciseContentFragment, FilesSolutionInput>;

export function FlaskExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const [correctExercise, correctionMutationResult] = useFlaskCorrectionMutation();

  const part = FlaskExercisePart.FlaskSingleExPart;

  const exerciseDescription = (
    <>
      <div className="mb-3" dangerouslySetInnerHTML={{__html: exercise.text}}/>

      <p>Es werden folgende Testfälle ausgeführt:</p>

      {content.testConfig.tests.map((singleTestConfig) =>
        <div key={singleTestConfig.id}>
          {singleTestConfig.id}. <code>{singleTestConfig.testName}</code>:
          <span dangerouslySetInnerHTML={{__html: singleTestConfig.description}}/>
        </div>
      )}
    </>
  );

  const initialFiles = oldSolution
    ? updateFileContents(oldSolution.files, content.files)
    : content.files;

  function correct(files: ExerciseFileFragment[], onCorrect: () => void): void {
    const solution: FilesSolutionInput = {
      files: files.map(({name, content, fileType, editable}) => ({name, content, fileType, editable}))
    };

    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution, part}})
      .then(onCorrect)
      .catch((err) => console.error(err));
  }

  const correctionTabRender = (
    <WithQuery query={correctionMutationResult}>
      {({flaskExercise}) => <WithNullableNavigate t={flaskExercise}>
        {({correct: {solutionSaved,/*proficienciesUpdated, resultSaved,*/ result}}) => <>
          <SolutionSaved solutionSaved={solutionSaved}/>

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
      initialFiles={initialFiles}
      sampleSolutions={content.flaskSampleSolutions}
      correct={correct}
      correctionTabRender={correctionTabRender}
      isCorrecting={correctionMutationResult.called && correctionMutationResult.loading}/>
  );
}
