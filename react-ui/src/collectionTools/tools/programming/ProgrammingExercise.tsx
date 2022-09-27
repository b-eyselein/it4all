import {ConcreteExerciseWithPartsProps} from '../../Exercise';
import {
  FilesSolutionInput,
  ImplementationCorrectionResultFragment,
  ProgExPart,
  ProgrammingExerciseContentFragment,
  UnitTestCorrectionResultFragment,
  useProgrammingCorrectionMutation
} from '../../../graphql';
import {FilesExercise} from '../FilesExercise';
import {database} from '../../DexieTable';
import {WithQuery} from '../../../WithQuery';
import {WithNullableNavigate} from '../../../WithNullableNavigate';
import {IExerciseFile} from '../../exerciseFile';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {textColors} from '../../../consts';
import classNames from 'classnames';

type IProps = ConcreteExerciseWithPartsProps<ProgrammingExerciseContentFragment, FilesSolutionInput>;

function ImplementationResult({result}: { result: ImplementationCorrectionResultFragment }): JSX.Element {
  return (
    <div className={classNames('p-2', 'rounded', 'border', 'border-slate-300', 'bg-slate-50', result.successful ? textColors.correct : textColors.inCorrect)}>
      <pre>{result.stderr.join('\n')}</pre>
    </div>
  );
}

export function UnitTestResult({result}: { result: UnitTestCorrectionResultFragment }): JSX.Element {

  const {testId, shouldFail, description, stderr, successful} = result;

  return (
    <li className="my-4">
      <span className={successful ? textColors.correct : textColors.inCorrect}>
        Der {testId}. Test war {successful ? '' : ' nicht'} erfolgreich.
        Der Test sollte {shouldFail ? '' : ' nicht'} fehlschlagen.
      </span>
      {!successful && <>
        <p className="p-2">Beschreibung: {description}</p>
        <pre className="p-2 rounded border border-slate-300">{stderr.join('\n')}</pre>
      </>}
    </li>
  );
}

export function ProgrammingExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const [correctExercise, correctionMutationResult] = useProgrammingCorrectionMutation();

  const part = partId === 'testCreation'
    ? ProgExPart.TestCreation
    : ProgExPart.Implementation;

  const defaultSolution = part === ProgExPart.TestCreation
    ? content.unitTestPart.unitTestFiles
    : content.implementationPart.files;

  function correct(files: IExerciseFile[], onCorrect: () => void): void {
    // remove other fields
    const solution: FilesSolutionInput = {
      files: files.map(({name, content, editable}) => ({name, content, editable}))
    };

    database.upsertSolutionWithParts(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution, part}})
      .then(onCorrect)
      .catch((err) => console.error(err));
  }

  const correctionTabRender = (
    <WithQuery query={correctionMutationResult}>
      {({programmingExercise}) => <WithNullableNavigate t={programmingExercise}>
        {({correct: {result/*,solutionId*/}}) => <>
          <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

          {result.unitTestResults.length > 0 && <ul className="list-disc list-inside">
            {result.unitTestResults.map((unitTestResult, index) => <UnitTestResult key={index} result={unitTestResult}/>)}
          </ul>}

          {result.implementationCorrectionResult && <ImplementationResult result={result.implementationCorrectionResult}/>}
        </>}
      </WithNullableNavigate>}
    </WithQuery>
  );

  return (
    <FilesExercise
      exerciseDescription={<p dangerouslySetInnerHTML={{__html: exercise.text}}/>}
      defaultFiles={defaultSolution}
      oldSolution={oldSolution}
      sampleSolutions={content.programmingSampleSolutions}
      correct={correct}
      correctionTabRender={correctionTabRender}
      isCorrecting={correctionMutationResult.loading}/>
  );
}
