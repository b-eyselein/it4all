import {GradedElementSpecResult, GradedTextContentResultFragment} from '../../../graphql';

interface IProps {
  elementSpecResult: GradedElementSpecResult;
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

export function ElementSpecResultDisplay({elementSpecResult}: IProps): JSX.Element {

  const {elementFound, attributeResults, textContentResult} = elementSpecResult;

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
