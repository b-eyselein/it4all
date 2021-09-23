import React from 'react';
import {ConcreteExerciseIProps} from '../../Exercise';
import {
  ExerciseFileFragment,
  FilesSolutionInput,
  ProgExPart,
  ProgrammingCorrectionMutation,
  ProgrammingExerciseContentFragment,
  useProgrammingCorrectionMutation
} from '../../../graphql';
import {FilesExercise} from '../FilesExercise';
import {database} from '../../DexieTable';
import {WithQuery} from '../../../WithQuery';
import {useTranslation} from 'react-i18next';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {UnitTestResult} from './UnitTestResult';
import {ImplementationResult} from './ImplementationResult';

type IProps = ConcreteExerciseIProps<ProgrammingExerciseContentFragment, FilesSolutionInput>;

export function ProgrammingExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [correctExercise, correctionMutationResult] = useProgrammingCorrectionMutation();

  if (!content.programmingPart) {
    throw new Error('TODO!');
  }

  const part = content.programmingPart;

  const initialFiles = oldSolution
    ? oldSolution.files
    : (part === ProgExPart.TestCreation ? content.unitTestPart.unitTestFiles : content.implementationPart.files);

  function correct(files: ExerciseFileFragment[], onCorrect: () => void): void {
    const solution: FilesSolutionInput = {
      files: files.map(({name, content, fileType, editable}) => ({name, content, fileType, editable}))
    };

    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution, part}})
      .then(onCorrect)
      .catch((err) => console.error(err));
  }

  function renderCorrection({programmingExercise}: ProgrammingCorrectionMutation) {
    if (!programmingExercise) {
      return <div className="notification is-danger has-text-centered">{t('errorWhileCorrecting')}</div>;
    }

    const {solutionSaved, /*proficienciesUpdated, resultSaved,*/ result} = programmingExercise.correct;

    return (
      <>
        <SolutionSaved solutionSaved={solutionSaved}/>

        <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

        {result.unitTestResults.length > 0 && <ul>
          {result.unitTestResults.map((unitTestResult, index) => <UnitTestResult key={index} result={unitTestResult}/>)}
        </ul>}

        {result.implementationCorrectionResult && <ImplementationResult result={result.implementationCorrectionResult}/>}
      </>
    );
  }

  const correctionTabRender = <WithQuery query={correctionMutationResult} render={renderCorrection}/>;

  return <FilesExercise exerciseId={exercise.exerciseId} exerciseDescription={<p dangerouslySetInnerHTML={{__html: exercise.text}}/>}
                        initialFiles={initialFiles} sampleSolutions={content.programmingSampleSolutions}
                        correct={correct} correctionTabRender={correctionTabRender}
                        isCorrecting={correctionMutationResult.loading}/>;
}
