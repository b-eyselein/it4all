import React from 'react';
import {GradedElementSpecResult, GradedTextContentResultFragment, SuccessType} from '../../../graphql';

interface IProps {
  htmlResult: GradedElementSpecResult;
}

function HtmlAttributeResultDisplay({attributeResult}: { attributeResult: GradedTextContentResultFragment }): JSX.Element {

  const {isSuccessful, keyName, maybeFoundContent, awaitedContent} = attributeResult;

  return (
    <li className={isSuccessful ? 'has-text-success' : 'has-text-danger'}>
      Das Attribut <code>{keyName}</code> {maybeFoundContent
      ? <>sollte den Wert <code>{awaitedContent}</code> haben. Gefunden wurde <code>{maybeFoundContent}</code>.</>
      : <>wurde nicht gefunden!</>}
    </li>
  );
}

export function ElementSpecResultDisplay({htmlResult}: IProps): JSX.Element {

  const {elementFound, attributeResults, textContentResult} = htmlResult;

  return (
    <ul>
      <li className={elementFound ? 'has-text-success' : 'has-text-danger'}>
        Das Element konnte {elementFound ? '' : 'nicht'} gefunden werden!
      </li>

      {attributeResults.map((attributeResult, index) => <HtmlAttributeResultDisplay attributeResult={attributeResult} key={index}/>)}

      {textContentResult && <li className={textContentResult.isSuccessful ? 'has-text-success' : 'has-text-danger'}>
        Das Element sollte den Textinhalt <code>{textContentResult.awaitedContent}</code> haben.
        Gefunden wurde <code>{textContentResult.maybeFoundContent}</code>.
      </li>
      }
    </ul>
  );
}

export function HtmlTaskResultDisplay({htmlResult}: IProps): JSX.Element {

  const {id, success, points, maxPoints, elementFound, attributeResults, textContentResult} = htmlResult;

  const isSuccessful = success === SuccessType.Complete;

  return (
    <>
      <p className={isSuccessful ? 'has-text-success' : 'has-text-danger'}>
        ({points} / {maxPoints}) Teilaufgabe {id} ist {isSuccessful ? '' : 'nicht'} korrekt:
      </p>

      <ElementSpecResultDisplay htmlResult={htmlResult}/>
    </>
  );
}
