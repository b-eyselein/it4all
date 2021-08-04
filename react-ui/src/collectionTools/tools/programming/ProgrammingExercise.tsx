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

type IProps = ConcreteExerciseIProps<ProgrammingExerciseContentFragment, FilesSolutionInput>;


export function ProgrammingExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [correctExercise, correctionMutationResult] = useProgrammingCorrectionMutation();

  if (!content.programmingPart) {
    throw new Error('TODO!');
  }

  const part = content.programmingPart;

  const files = oldSolution
    ? oldSolution.files
    : (part === ProgExPart.TestCreation ? content.unitTestPart.unitTestFiles : content.implementationPart.files);

  function correct(files: ExerciseFileFragment[]): void {
    const solution: FilesSolutionInput = {
      files: files.map(({name, content, fileType, editable}) => ({name, content, fileType, editable}))
    };

    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution, part}})
      .catch((err) => console.error(err));
  }

  function correctionTabRender({programmingExercise}: ProgrammingCorrectionMutation) {

    if (!programmingExercise) {
      return <div className="notification is-danger has-text-centered">{t('errorWhileCorrecting')}</div>;
    }

    const {solutionSaved, /*proficienciesUpdated, resultSaved,*/ result} = programmingExercise.correct;

    return <div>TODO!</div>;
  }

  return <FilesExercise exerciseId={exercise.exerciseId} exerciseDescription={<p dangerouslySetInnerHTML={{__html: exercise.text}}/>} initialFiles={files}
                        sampleSolutions={content.programmingSampleSolutions}
                        correct={correct} correctionTabRender={() => <WithQuery query={correctionMutationResult} render={correctionTabRender}/>}
                        isCorrecting={correctionMutationResult.loading}/>;
}
