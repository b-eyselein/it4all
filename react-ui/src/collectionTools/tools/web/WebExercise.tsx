import React from 'react';
import {ConcreteExerciseIProps} from '../../Exercise';
import {
  ExerciseFileFragment,
  FilesSolutionInput,
  useWebCorrectionMutation,
  WebCorrectionMutation,
  WebExerciseContentFragment,
  WebExPart
} from '../../../graphql';
import {FilesExercise} from '../FilesExercise';
import {WithQuery} from '../../../WithQuery';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {useTranslation} from 'react-i18next';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {HtmlTaskResultDisplay} from './HtmlTaskResultDisplay';
import {database} from '../../DexieTable';

type IProps = ConcreteExerciseIProps<WebExerciseContentFragment, FilesSolutionInput>;

export function WebExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [correctExercise, correctionMutationResult] = useWebCorrectionMutation();

  if (!content.webPart) {
    throw new Error('TODO');
  }

  const part = content.webPart;

  const exerciseDescription = (
    <div className="content">
      <p>{exercise.text}</p>

      {part === WebExPart.HtmlPart
        ? <ol>{content.siteSpec.htmlTasks.map((task, index) => <li key={index}>{task.text}</li>)}</ol>
        : <p>Es gibt insgesamt {content.siteSpec.jsTaskCount} Testfälle.</p>
      }
    </div>
  );

  const initialFiles = oldSolution
    ? oldSolution.files
    : content.files;

  function correct(files: ExerciseFileFragment[]): void {
    const solution: FilesSolutionInput = {
      files: files.map(({name, content, fileType, editable}) => ({name, content, fileType, editable}))
    };

    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution, part}})
      .catch((err) => console.error(err));
  }

  function renderCorrection({webExercise}: WebCorrectionMutation): JSX.Element {
    if (!webExercise) {
      return <div className="notification is-danger has-text-centered">{t('errorWhileCorrection...')}</div>;
    }

    const {solutionSaved,/*proficienciesUpdated,resultSaved,*/result} = webExercise.correct;

    return (
      <>
        <SolutionSaved solutionSaved={solutionSaved}/>

        <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

        <div className="content my-3">
          {result.gradedHtmlTaskResults.length > 0 && <ul>
            {result.gradedHtmlTaskResults.map((htmlTaskResult, index) =>
              <li key={index}>
                <HtmlTaskResultDisplay htmlResult={htmlTaskResult}/>
              </li>
            )}
          </ul>}

          {result.gradedJsTaskResults.length > 0 && <ul>
            {result.gradedJsTaskResults.map((jsResult, index) =>
              <li key={index} className={jsResult.success === 'COMPLETE' ? 'has-text-success' : 'has-text-danger'}>
                ({jsResult.points} / {jsResult.maxPoints}) Test {jsResult.id} ist {jsResult.success === 'COMPLETE' ? '' : 'nicht'} korrekt:

                {JSON.stringify(jsResult)}
              </li>
            )}
          </ul>
          }
        </div>

      </>
    );
  }

  const correctionTabRender = <WithQuery query={correctionMutationResult} render={renderCorrection}/>;

  return <FilesExercise exerciseId={exercise.exerciseId} exerciseDescription={exerciseDescription} initialFiles={initialFiles}
                        sampleSolutions={content.webSampleSolutions} isCorrecting={correctionMutationResult.loading}
                        correctionTabRender={correctionTabRender} correct={correct}/>;
}
