import React from 'react';
import {XmlDocumentResultFragment} from '../../../graphql';

interface IProps {
  result: XmlDocumentResultFragment;
}

export function XmlDocumentResultDisplay({result}: IProps): JSX.Element {

  const isCorrect = result.errors.length === 0;

  return (
    <>
      <p className={isCorrect ? 'has-text-dark-success' : 'has-text-danger'}>
        Die Korrektur war {isCorrect ? '' : 'nicht'} erfolgreich. Es wurden {isCorrect ? 'keine' : result.errors.length} Fehler gefunden{isCorrect ? '.' : ':'}
      </p>

      {!isCorrect && <div className="content">
        <ul>
          {result.errors.map((err, index) =>
            <li className={err.errorType === 'WARNING' ? 'has-text-dark-warning' : 'has-text-danger'} key={index}>
              <b>Fehler in Zeile {err.line}</b>: <code>{err.errorMessage}</code>
            </li>
          )}
        </ul>
      </div>}
    </>
  );
}
