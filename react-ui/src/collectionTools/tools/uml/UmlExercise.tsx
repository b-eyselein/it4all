import React from 'react';
import {ConcreteExerciseIProps} from '../../Exercise';
import {UmlExerciseContentFragment} from '../../../graphql';

type IProps = ConcreteExerciseIProps<UmlExerciseContentFragment, any>;

export function UmlExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {
  return <div>TODO!</div>;
}
