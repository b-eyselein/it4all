import {FilesSolutionInput, FlaskExerciseContentFragment, useFlaskCorrectionMutation} from '../../../graphql';
import {ConcreteExerciseWithoutPartsProps} from '../../Exercise';
import {FilesExercise} from '../FilesExercise';
import {WithQuery} from '../../../WithQuery';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {database} from '../../DexieTable';
import {WithNullableNavigate} from '../../../WithNullableNavigate';
import {IExerciseFile} from '../../exerciseFile';
import {FlaskExerciseDescription} from './FlaskExerciseDescription';
import classNames from 'classnames';
import {bgColors, textColors} from '../../../consts';

type IProps = ConcreteExerciseWithoutPartsProps<FlaskExerciseContentFragment, FilesSolutionInput>;

export function FlaskExercise({exercise, content, oldSolution}: IProps): JSX.Element {

  const [correctExercise, correctionMutationResult] = useFlaskCorrectionMutation();

  const exerciseDescription = <FlaskExerciseDescription exercise={exercise} content={content}/>;

  async function correct(files: IExerciseFile[], onCorrect: () => void): Promise<void> {
    const solution: FilesSolutionInput = {
      files: files.map(({name, content, editable}) => ({name, content, editable}))
    };

    await database.upsertSolutionWithoutParts(exercise.toolId, exercise.collectionId, exercise.exerciseId, solution);

    await correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution}})
      .catch((err) => console.error(err));

    onCorrect();
  }

  const correctionTabRender = (
    <WithQuery query={correctionMutationResult}>
      {({flaskExercise}) => <WithNullableNavigate t={flaskExercise}>
        {({correct: {result/*, solutionId */}}) => <>

          <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

          {result.testResults.map((testResult, index) =>
            <div key={index} className="my-4 rounded-t border border-slate-500">
              <header className={classNames('p-2', 'rounded-t', 'font-bold', 'text-white', testResult.successful ? bgColors.correct : bgColors.inCorrect)}>
                {testResult.successful ? <span>&#10004;</span> : <span>&#10008;</span>} {testResult.testName}
              </header>
              <pre className={classNames('p-2', 'font-mono', testResult.successful ? textColors.correct : textColors.inCorrect)}>
                {testResult.stderr.join('\n')}
              </pre>
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
