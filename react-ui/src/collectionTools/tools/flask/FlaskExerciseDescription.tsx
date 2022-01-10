import {ExerciseSolveFieldsFragment, FlaskExerciseContentFragment} from '../../../graphql';

interface IProps {
  exercise: ExerciseSolveFieldsFragment;
  content: FlaskExerciseContentFragment;
}

export function FlaskExerciseDescription({exercise, content}: IProps): JSX.Element {
  return (
    <>
      <div className="mb-3" dangerouslySetInnerHTML={{__html: exercise.text}}/>

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
