import React from 'react';
import {ExerciseIProps} from '../ToolBase';
import {Redirect, useRouteMatch} from 'react-router-dom';
import {ExerciseQuery, ExerciseSolveFieldsFragment, useExerciseQuery} from '../graphql';
import {WithQuery} from '../WithQuery';
import {EbnfExercise} from './tools/ebnf/EbnfExercise';
import {RegexExercise} from './tools/regex/RegexExercise';
import {SqlExercise} from './tools/sql/SqlExercise';
import {ProgrammingExercise} from './tools/programming/ProgrammingExercise';
import {WebExercise} from './tools/web/WebExercise';
import {XmlExercise} from './tools/xml/XmlExercise';
import {FlaskExercise} from './tools/flask/FlaskExercise';

export interface ConcreteExerciseIProps<T> {
  exercise: ExerciseSolveFieldsFragment;
  content: T;
}

export function Exercise({toolId, collectionId, exerciseId}: ExerciseIProps): JSX.Element {

  const partId = useRouteMatch<{ partId: string }>().params.partId;

  const exerciseQuery = useExerciseQuery({variables: {toolId, collectionId, exerciseId, partId}});

  function render({tool}: ExerciseQuery): JSX.Element {

    if (!tool) {
      return <Redirect to={''}/>;
    }

    const collection = tool.collection;

    if (!collection) {
      return <Redirect to={''}/>;
    }

    const exercise = collection.exercise;

    if (!exercise) {
      return <Redirect to={''}/>;
    }

    const content = exercise.content;


    if (content.__typename === 'EbnfExerciseContent') {
      return <EbnfExercise exercise={exercise} content={content}/>;
    } else if (content.__typename === 'FlaskExerciseContent') {
      return <FlaskExercise exercise={exercise} content={content}/>;
    } else if (content.__typename === 'ProgrammingExerciseContent') {
      return <ProgrammingExercise exercise={exercise} content={content}/>;
    } else if (content.__typename === 'RegexExerciseContent') {
      return <RegexExercise exercise={exercise} content={content}/>;
    } else if (content.__typename === 'SqlExerciseContent') {
      return <SqlExercise exercise={exercise} content={content}/>;
    } else if (content.__typename === 'WebExerciseContent') {
      return <WebExercise exercise={exercise} content={content}/>;
    } else if (content.__typename === 'XmlExerciseContent') {
      return <XmlExercise exercise={exercise} content={content}/>;
    } else {
      return <div>{JSON.stringify(content)}</div>;
    }
  }

  return <WithQuery query={exerciseQuery} render={render}/>;

}
