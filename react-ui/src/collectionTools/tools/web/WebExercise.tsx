import {ConcreteExerciseWithPartsProps} from '../../Exercise';
import {FilesSolutionInput, useWebCorrectionMutation, WebExerciseContentFragment, WebExPart} from '../../../graphql';
import {FilesExercise} from '../FilesExercise';
import {WithQuery} from '../../../WithQuery';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {JsTaskResultDisplay} from './JsTaskResultDisplay';
import {database} from '../../DexieTable';
import {WithNullableNavigate} from '../../../WithNullableNavigate';
import {IExerciseFile} from '../../exerciseFile';
import {textColors} from '../../../consts';
import {ElementSpecResultDisplay} from './ElementSpecResultDisplay';

type IProps = ConcreteExerciseWithPartsProps<WebExerciseContentFragment, FilesSolutionInput>;


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

  function correct(files: IExerciseFile[], onCorrect: () => void): void {
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
      {({webExercise}) => <WithNullableNavigate t={webExercise}>
        {({correct: {result/*, solutionId */}}) => <>
          <PointsNotification points={result.points} maxPoints={result.maxPoints}/>

          {result.gradedHtmlTaskResults.length > 0 &&
            <ul className="my-3 list-disc list-inside">
              {result.gradedHtmlTaskResults.map(({id, elementSpecResult}, index) =>
                <li key={index} className="my-4">
                  <span className={elementSpecResult.isCorrect ? textColors.correct : textColors.inCorrect}>
                    ({elementSpecResult.points} / {elementSpecResult.maxPoints} P) Teilaufgabe {id} ist {elementSpecResult.isCorrect ? '' : 'nicht'} korrekt:
                  </span>

                  <div className="ml-4">
                    <ElementSpecResultDisplay elementSpecResult={elementSpecResult}/>
                  </div>
                </li>)}
            </ul>}

          {result.gradedJsTaskResults.length > 0 && <ul className="my-3 list-disc list-inside">
            {result.gradedJsTaskResults.map((jsResult, index) =>
              <li key={index} className="my-4">
                <JsTaskResultDisplay jsResult={jsResult}/>
              </li>
            )}
          </ul>}

        </>}
      </WithNullableNavigate>}
    </WithQuery>
  );

  return (
    <FilesExercise
      exerciseDescription={exerciseDescription}
      defaultFiles={content.files}
      oldSolution={oldSolution}
      sampleSolutions={content.webSampleSolutions}
      isCorrecting={correctionMutationResult.loading}
      correctionTabRender={correctionTabRender}
      correct={correct}/>
  );
}
