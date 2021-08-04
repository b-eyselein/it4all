import React from 'react';
import {ConcreteExerciseIProps} from '../../Exercise';
import {EbnfExerciseContentFragment} from '../../../graphql';

type IProps = ConcreteExerciseIProps<EbnfExerciseContentFragment, string>;

export function EbnfExercise({/*exercise,*/ content, /*partId, oldSolution*/}: IProps): JSX.Element {
  return (
    <div className="container">
      <div className="columns">
        {JSON.stringify(content, null, 2)}
      </div>
    </div>
  );
}
