import {ConcreteExerciseIProps} from '../../Exercise';
import {ExerciseFileFragment, FilesSolutionInput, useWebCorrectionMutation, WebExerciseContentFragment, WebExPart} from '../../../graphql';
import {FilesExercise, updateFileContents} from '../FilesExercise';
import {WithQuery} from '../../../WithQuery';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {HtmlTaskResultDisplay} from './HtmlTaskResultDisplay';
import {JsTaskResultDisplay} from './JsTaskResultDisplay';
import {database} from '../../DexieTable';
import {WithNullableNavigate} from '../../../WithNullableNavigate';

type IProps = ConcreteExerciseIProps<WebExerciseContentFragment, FilesSolutionInput>;


export function WebExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const [correctExercise, correctionMutationResult] = useWebCorrectionMutation();

  const part = partId === 'html' ? WebExPart.HtmlPart : WebExPart.JsPart;

  const exerciseDescription = (
    <div className="content">
      <p dangerouslySetInnerHTML={{__html: exercise.text}}/>

      {part === WebExPart.HtmlPart
        ? <ol>{content.siteSpec.htmlTasks.map((task, index) => <li key={index}>{task.text}</li>)}</ol>
        : <p>Es gibt insgesamt {content.siteSpec.jsTaskCount} Testfälle.</p>
      }
    </div>
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
      {({webExercise}) => <WithNullableNavigate t={webExercise}>
        {({correct: {solutionSaved,/*proficienciesUpdated,resultSaved,*/result}}) => <>
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
                <li key={index}>
                  <JsTaskResultDisplay jsResult={jsResult}/>
                </li>
              )}
            </ul>
            }
          </div>

        </>}
      </WithNullableNavigate>}
    </WithQuery>
  );

  return (
    <FilesExercise
      exerciseDescription={exerciseDescription}
      initialFiles={initialFiles}
      sampleSolutions={content.webSampleSolutions}
      isCorrecting={correctionMutationResult.loading}
      correctionTabRender={correctionTabRender}
      correct={correct}/>
  );
}
