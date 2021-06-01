import React from "react";
import {ExerciseIProps} from "../ToolBase";
import {Redirect, useRouteMatch} from "react-router-dom";
import {ExerciseQuery, useExerciseQuery} from "../generated/graphql";
import {WithQuery} from "../WithQuery";
import {RegexExercise} from './tools/regex/RegexExercise';
import {SqlExercise} from './tools/sql/SqlExercise';

export function Exercise({toolId, collectionId, exerciseId}: ExerciseIProps): JSX.Element {

  const partId = useRouteMatch<{ partId: string }>().params.partId;

  const exerciseQuery = useExerciseQuery({variables: {toolId, collectionId, exerciseId, partId}});

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
      return <RegexExercise exerciseFragment={exercise} contentFragment={content}/>;
    } else if (content.__typename === 'SqlExerciseContent') {
      return <SqlExercise exerciseFragment={exercise} contentFragment={content}/>;
    } else {
      return <div>{JSON.stringify(exercise)}</div>
    }
  }

  return <WithQuery query={exerciseQuery} children={render}/>;

}
