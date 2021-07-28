import React from 'react';
import {GradedHtmlTaskResult, GradedTextContentResultFragment} from '../../../graphql';

interface IProps {
  htmlResult: GradedHtmlTaskResult;
}


function HtmlAttributeResultDisplay({attributeResult}: { attributeResult: GradedTextContentResultFragment }): JSX.Element {
  return (
    <li className={attributeResult.isSuccessful ? 'has-text-success' : 'has-text-danger'}>
      Das Attribut <code>{attributeResult.keyName}</code>
      {attributeResult.maybeFoundContent
        ? <>
          sollte den Wert <code>{attributeResult.awaitedContent}</code> haben.
          Gefunden wurde <code>{attributeResult.maybeFoundContent}</code>.
        </>
        : <>wurde nicht gefunden!</>}
    </li>
  );
}

export function HtmlTaskResultDisplay({htmlResult}: IProps): JSX.Element {
  return (
    <>
      <span className={htmlResult.isSuccessful ? 'has-text-success' : 'has-text-danger'}>
      ({htmlResult.points} / {htmlResult.maxPoints}) Teilaufgabe {htmlResult.id} ist {htmlResult.isSuccessful ? '' : 'nicht'} korrekt:
    </span>

      <ul>
        <li className={htmlResult.elementFound ? 'has-text-success' : 'has-text-danger'}>
          Das Element konnte {htmlResult.elementFound ? '' : 'nicht'} gefunden werden!
        </li>

        {htmlResult.attributeResults.map((attributeResult, index) =>
          <HtmlAttributeResultDisplay attributeResult={attributeResult} key={index}/>
        )}

        {htmlResult.textContentResult &&
        <li className={htmlResult.textContentResult.isSuccessful ? 'has-text-success' : 'has-text-danger'}>
          Das Element sollte den Textinhalt <code>{htmlResult.textContentResult.awaitedContent}</code> haben.
          Gefunden wurde <code>{htmlResult.textContentResult.maybeFoundContent}</code>.
        </li>
        }
      </ul>
    </>
  );
}
