import React from 'react';
import {MatchType, XmlElementLineMatchFragment, XmlElementLineMatchingResultFragment} from '../../../graphql';


interface IProps {
  result: XmlElementLineMatchingResultFragment;
}

function XmlElementLineMatchResultDisplay({m}: { m: XmlElementLineMatchFragment }): JSX.Element {

  const isCorrect = m.matchType === MatchType.SuccessfulMatch;

  return (
    <div className="content">
      <div className={isCorrect ? 'has-text-dark-success' : 'has-text-danger'}>
        Die Definition des Element <code>{m.userArg.elementName}</code> ist {isCorrect ? '' : 'nicht'} korrekt.

        {m.analysisResult && !isCorrect &&
        <ul>
          <li className={m.analysisResult.contentCorrect ? 'has-text-dark-success' : 'has-text-danger'}>
            Der Inhalt des Elements war {m.analysisResult.contentCorrect ? '' : 'nicht'} korrekt.

            {!m.analysisResult.contentCorrect && <>Erwartet wurde <code>{m.analysisResult.correctContent}</code></>}
          </li>
          <li className={m.analysisResult.attributesCorrect ? 'has-text-dark-success' : 'has-text-danger'}>
            Die Attribute des Elements waren {m.analysisResult.attributesCorrect ? '' : 'nicht'} korrekt.

            {!m.analysisResult.attributesCorrect && <>Erwartet wurde <code>{m.analysisResult.correctAttributes}</code>.</>}
          </li>
        </ul>
        }

      </div>
    </div>
  );
}

export function XmlElementLineMatcingResulthDisplay({result}: IProps): JSX.Element {
  return (
    <>
      {result.allMatches.map((m, index) => <XmlElementLineMatchResultDisplay m={m} key={index}/>)}

      {result.notMatchedForUser.map((mu, index) =>
        <div className="has-text-danger" key={index}>
          Die Definition des Elements <code>{mu.elementName}</code> ist falsch.
        </div>
      )}

      {result.notMatchedForSample.map((ms, index) =>
        <div className="has-text-danger" key={index}>
          Die Definition des Elements <code>{ms.elementName}</code> fehlt.
        </div>
      )}
    </>
  );
}
