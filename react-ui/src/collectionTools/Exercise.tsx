import React, {useState} from 'react';
import {ExerciseIProps} from '../ToolBase';
import {Redirect, useRouteMatch} from 'react-router-dom';
import {ExerciseQuery, ExerciseSolveFieldsFragment, useExerciseQuery} from '../graphql';
import {WithQuery} from '../WithQuery';
import {RegexExercise} from './tools/regex/RegexExercise';
import {SqlExercise} from './tools/sql/SqlExercise';


export interface ConcreteExerciseIProps<T> {
  exerciseFragment: ExerciseSolveFieldsFragment;
  contentFragment: T;
  showSampleSolutions: boolean;
  toggleSampleSolutions: () => void;
}

export function Exercise({toolId, collectionId, exerciseId}: ExerciseIProps): JSX.Element {

  const partId = useRouteMatch<{ partId: string }>().params.partId;

  const [showSampleSolution, setShowSampleSolution] = useState(false);

  const exerciseQuery = useExerciseQuery({variables: {toolId, collectionId, exerciseId, partId}});

  function toggleShowSampleSolutions(): void {
    setShowSampleSolution((show) => !show);
  }

  function render({me}: ExerciseQuery): JSX.Element {
    if (!me || !me.tool || !me.tool.collection || !me.tool.collection.exercise) {
      return <Redirect to={'/'}/>;
    }

    const exercise = me.tool.collection.exercise;
    const content = exercise.content;


    /*if (content.__typename === 'EbnfExerciseContent') {
      return <div></div>
    } else*/
    if (content.__typename === 'RegexExerciseContent') {
      return <RegexExercise exerciseFragment={exercise} contentFragment={content} showSampleSolutions={showSampleSolution}
                            toggleSampleSolutions={toggleShowSampleSolutions}/>;
    } else if (content.__typename === 'SqlExerciseContent') {
      return <SqlExercise exerciseFragment={exercise} contentFragment={content} showSampleSolutions={showSampleSolution}
                          toggleSampleSolutions={toggleShowSampleSolutions}/>;
    } else {
      return <div>{JSON.stringify(exercise)}</div>;
    }
  }

  return <WithQuery query={exerciseQuery} render={render}/>;

}
