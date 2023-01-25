import { Fragment } from 'react';
import {ExerciseSolveFieldsFragment, UmlExerciseContentFragment} from '../../../graphql';
import {getUmlExerciseTextParts} from './uml-helpers';

interface IProps {
  exercise: ExerciseSolveFieldsFragment;
  content: UmlExerciseContentFragment;
  onClassClick: (name: string) => void;
}

export function UmlExerciseText({exercise, content, onClassClick}: IProps): JSX.Element {

  const textParts = getUmlExerciseTextParts(exercise, content);

  return (
    <div className="my-4 p-2 rounded bg-gray-200">
      {textParts.map((textPart, index) =>
        <Fragment key={index}>
          {typeof textPart !== 'string'
            ? <span onClick={() => onClassClick(textPart.className)}
              /*className={state.selectedClasses.includes(textPart.className) ? 'has-text-link' : 'has-text-black'}*/>{textPart.text}</span>
            : <span>{textPart}</span>}
        </Fragment>
      )}
    </div>
  );
}
