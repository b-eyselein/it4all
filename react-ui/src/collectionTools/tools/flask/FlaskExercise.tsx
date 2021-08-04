import React from 'react';
import {
  ExerciseFileFragment,
  FilesSolutionInput,
  FlaskCorrectionMutation,
  FlaskExerciseContentFragment,
  FlaskExercisePart,
  useFlaskCorrectionMutation
} from '../../../graphql';
import {ConcreteExerciseIProps} from '../../Exercise';
import {FilesExercise} from '../FilesExercise';
import {WithQuery} from '../../../WithQuery';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {PointsNotification} from '../../../helpers/PointsNotification';
import classNames from 'classnames';
import {database} from '../../DexieTable';

type IProps = ConcreteExerciseIProps<FlaskExerciseContentFragment, FilesSolutionInput>;

export function FlaskExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const [correctExercise, correctionMutationResult] = useFlaskCorrectionMutation();

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
    ? oldSolution.files
    : content.files;

  function correct(files: ExerciseFileFragment[]): void {
    const solution: FilesSolutionInput = {
      files: files.map(({name, content, fileType, editable}) => ({name, content, fileType, editable}))
    };

    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution, part: FlaskExercisePart.FlaskSingleExPart}})
      .catch((err) => console.error(err));
  }

  function renderCorrection({flaskExercise}: FlaskCorrectionMutation): JSX.Element {
    if (!flaskExercise) {
      throw new Error('TODO!');
    }

    const {solutionSaved,/*proficienciesUpdated, resultSaved,*/ result} = flaskExercise.correct;

    return (
      <>
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
      </>
    );
  }

  return <FilesExercise exerciseId={exercise.exerciseId} exerciseDescription={exerciseDescription} initialFiles={initialFiles}
                        sampleSolutions={content.flaskSampleSolutions} correct={correct}
                        correctionTabRender={() => <WithQuery query={correctionMutationResult} render={renderCorrection}/>}
                        isCorrecting={correctionMutationResult.called && correctionMutationResult.loading}/>;
}
