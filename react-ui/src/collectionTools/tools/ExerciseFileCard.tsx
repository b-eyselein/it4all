import React from 'react';
import {ExerciseFileFragment} from '../../graphql';

interface IProps {
  exerciseFile: ExerciseFileFragment;
}

export function ExerciseFileCard({exerciseFile}: IProps): JSX.Element {
  return (
    <div className="card">
      <header className="card-header">
        <p className="card-header-title">{exerciseFile.name}</p>
      </header>
      <div className="card-content">
        <pre>{exerciseFile.content}</pre>
      </div>
    </div>
  );
}
