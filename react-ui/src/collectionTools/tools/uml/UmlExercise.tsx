import React from 'react';
import {ConcreteExerciseIProps} from '../../Exercise';
import {UmlClassDiagramInput, UmlExerciseContentFragment} from '../../../graphql';

type IProps = ConcreteExerciseIProps<UmlExerciseContentFragment, UmlClassDiagramInput>;

export function UmlExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {
  return <div>TODO!</div>;
}
