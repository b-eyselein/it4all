import React, {useState} from 'react';
import * as joint from 'jointjs';
import {MyJointClass} from './model/joint-class-diag-elements';

interface IProps {
  editedAssociation: joint.shapes.uml.Association;
  cancelEdit: () => void;
}

type Cardinality = '1' | '*';

interface IState {
  firstMult: Cardinality;
  secondMult: Cardinality;
}

export function UmlAssocEdit({editedAssociation, cancelEdit}: IProps): JSX.Element {

  const labels = editedAssociation.labels();

  const firstEnd = editedAssociation.getSourceCell() as MyJointClass;
  const secondEnd = editedAssociation.getTargetCell() as MyJointClass;

  const [state, setState] = useState<IState>({
    firstMult: (labels[0].attrs?.text?.text || '*') as Cardinality,
    secondMult: (labels[1].attrs?.text?.text || '*') as Cardinality
  });

  function updateAssoc(): void {
    editedAssociation.label(0, {position: 25, attrs: {text: {text: state.firstMult}}});
    editedAssociation.label(1, {position: -25, attrs: {text: {text: state.secondMult}}});

    cancelEdit();
  }

  return (
    <div className="card">
      <header className="card-header">
        <p className="card-header-title">Assoziation bearbeiten</p>
      </header>
      <div className="card-content">
        <div className="columns">
          <div className="column">{firstEnd.getClassName()}</div>
          <div className="column">
            <div className="select">
              <select title="Multiplizität" defaultValue={state.firstMult}
                      onChange={(event) => setState(({secondMult}) => ({secondMult, firstMult: event.target.value as Cardinality}))}>
                <option>1</option>
                <option>*</option>
              </select>
            </div>
          </div>
          <div className="column">
            <div className="select">
              <select defaultValue={state.secondMult}
                      onChange={(event) => setState(({firstMult}) => ({firstMult, secondMult: event.target.value as Cardinality}))}>
                <option>1</option>
                <option>*</option>
              </select>
            </div>
          </div>
          <div className="column">{secondEnd.getClassName()}</div>
        </div>
      </div>
      <footer className="card-footer">
        <a className="card-footer-item" onClick={cancelEdit}>Verwerfen</a>
        <a className="card-footer-item" onClick={updateAssoc}>Anwenden</a>
      </footer>
    </div>
  );
}
