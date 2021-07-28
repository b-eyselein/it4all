import React from 'react';
import {FlaskExerciseContentFragment} from '../../../graphql';
import {ConcreteExerciseIProps} from '../../Exercise';
import {FilesExercise} from '../FilesExercise';

type IProps = ConcreteExerciseIProps<FlaskExerciseContentFragment>;

export function FlaskExercise({exercise, content}: IProps): JSX.Element {

  const exerciseDescription = <>
    <div className="mb-3" dangerouslySetInnerHTML={{__html: exercise.text}}/>

    <p>Es werden folgende Testfälle ausgeführt:</p>

    {content.testConfig.tests.map((singleTestConfig) =>
      <div key={singleTestConfig.id}>
        {singleTestConfig.id}. <code>{singleTestConfig.testName}</code>:
        <span dangerouslySetInnerHTML={{__html: singleTestConfig.description}}/>
      </div>
    )}
  </>;

  function correct(): void {
    console.error('TODO: correct...');
  }

  function correctionTabRender() {
    return <div>TODO!</div>;
  }

  return <FilesExercise exerciseId={exercise.exerciseId} exerciseDescription={exerciseDescription} initialFiles={content.files}
                        sampleSolutions={content.flaskSampleSolutions} correct={correct} correctionTabRender={correctionTabRender}
                        isCorrecting={false /*TODO!*/}/>;
}
