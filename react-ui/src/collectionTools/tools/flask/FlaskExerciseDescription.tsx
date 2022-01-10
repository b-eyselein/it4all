import {ExerciseSolveFieldsFragment, FlaskExerciseContentFragment} from '../../../graphql';

interface IProps {
  exercise: ExerciseSolveFieldsFragment;
  content: FlaskExerciseContentFragment;
}

export function FlaskExerciseDescription({exercise, content}: IProps): JSX.Element {

  const {text, newExerciseText} = exercise;

  console.info(JSON.stringify(newExerciseText, null, 2));

  const x: 'ExerciseTextTextParagraph' | 'ExerciseTextListParagraph' = newExerciseText[0].__typename;

  for (const textParagraph of newExerciseText) {
    if (textParagraph.__typename === 'ExerciseTextListParagraph') {
      const points: Array<{ __typename?: 'BulletListPoint'; textParts: Array<{ __typename: 'HighlightedTextPart'; text: string } | { __typename: 'StringTextPart'; text: string }> }> = textParagraph.points;

      console.info(points);
    } else {
      const textParts: Array<{ __typename: 'HighlightedTextPart'; text: string } | { __typename: 'StringTextPart'; text: string }> = textParagraph.textParts;

      console.info(textParts);
    }
  }

  /*
  if (newExerciseText.length > 0) {
    return (
      <>
        {JSON.stringify()
        < />
          )
        }
   */

  return (
    <>
      <div className="mb-3" dangerouslySetInnerHTML={{__html: text}}/>

      <p>Es werden folgende Testfälle ausgeführt:</p>

      {content.testConfig.tests.map((singleTestConfig) =>
        <div key={singleTestConfig.id}>
          {singleTestConfig.id}. <code>{singleTestConfig.testName}</code>:
          <span dangerouslySetInnerHTML={{__html: singleTestConfig.description}}/>
        </div>
      )}
    </>
  );
}
