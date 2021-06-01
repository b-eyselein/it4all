import React from "react";
import {ExerciseSolveFieldsFragment, FlaskExerciseContentFragment} from "../../generated/graphql";

interface IProps {
  exerciseFragment: ExerciseSolveFieldsFragment;
  contentFragment: FlaskExerciseContentFragment;
}

export function FlaskExercise({exerciseFragment,contentFragment}: IProps): JSX.Element {
  return <div>TODO!</div>;
}
