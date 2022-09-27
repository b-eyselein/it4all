import {ConcreteExerciseWithPartsProps} from '../../Exercise';
import {FilesSolutionInput, ProgExPart, ProgrammingExerciseContentFragment, useProgrammingCorrectionMutation} from '../../../graphql';
import {FilesExercise} from '../FilesExercise';
import {database} from '../../DexieTable';
import {WithQuery} from '../../../WithQuery';
import {UnitTestResult} from './UnitTestResult';
import {ImplementationResult} from './ImplementationResult';
import {WithNullableNavigate} from '../../../WithNullableNavigate';
import {IExerciseFile} from '../../exerciseFile';
import {PointsNotification} from '../../../helpers/PointsNotification';

type IProps = ConcreteExerciseWithPartsProps<ProgrammingExerciseContentFragment, FilesSolutionInput>;

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
