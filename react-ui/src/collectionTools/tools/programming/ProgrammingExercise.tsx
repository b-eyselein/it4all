import React from 'react';
import {ConcreteExerciseIProps} from '../../Exercise';
import {ExerciseFileFragment, FilesSolutionInput, ProgExPart, ProgrammingExerciseContentFragment, useProgrammingCorrectionMutation} from '../../../graphql';
import {FilesExercise} from '../FilesExercise';

type IProps = ConcreteExerciseIProps<ProgrammingExerciseContentFragment, FilesSolutionInput>;


export function ProgrammingExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const [correctExercise, correctionMutationResult] = useProgrammingCorrectionMutation();

  if (!content.programmingPart) {
    throw new Error('TODO!');
  }

  const part = content.programmingPart;

  const files = part === ProgExPart.TestCreation ? content.unitTestPart.unitTestFiles : content.implementationPart.files;

  function correct(files: ExerciseFileFragment[]): void {
    files.forEach((f) => console.info(JSON.stringify(f, null, 2)));

    const solution: FilesSolutionInput = {
      files: files.map(({name, content, fileType, editable}) => ({name, content, fileType, editable}))
    };

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution, part}})
      .catch((err) => console.error(err));
  }

  function correctionTabRender() {

    correctionMutationResult.data && console.info(JSON.stringify(correctionMutationResult.data, null, 2));

    return <div>TODO!</div>;
  }

  return <FilesExercise exerciseId={exercise.exerciseId} exerciseDescription={<p dangerouslySetInnerHTML={{__html: exercise.text}}/>} initialFiles={files}
                        sampleSolutions={content.programmingSampleSolutions}
                        correct={correct} correctionTabRender={correctionTabRender}
                        isCorrecting={correctionMutationResult.loading}/>;
}
