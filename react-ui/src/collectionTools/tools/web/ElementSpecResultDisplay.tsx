import {GradedElementSpecResultFragment, GradedTextContentResultFragment} from '../../../graphql';
import {textColors} from '../../../consts';
import {useTranslation} from 'react-i18next';

interface IProps {
  elementSpecResult: GradedElementSpecResultFragment;
}

function HtmlAttributeResultDisplay({attributeResult}: { attributeResult: GradedTextContentResultFragment }): JSX.Element {

  const {isSuccessful, keyName, maybeFoundContent, awaitedContent} = attributeResult;

  return (
    <li className={isSuccessful ? textColors.correct : textColors.inCorrect}>
      Das Attribut <code>{keyName}</code> {maybeFoundContent
      ? <>sollte den Wert <code>{awaitedContent}</code> haben. Gefunden wurde <code>{maybeFoundContent}</code>.</>
      : <>wurde nicht gefunden!</>}
    </li>
  );
}

export function ElementSpecResultDisplay({elementSpecResult}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const {elementFound, attributeResults, textContentResult} = elementSpecResult;

  return (
    <ul className="list-disc list-inside">
      {elementFound
        ? <li className={textColors.correct}>{t('elementFount')}</li>
        : <li className={textColors.inCorrect}>{t('elementNotFound')}</li>}

      {attributeResults.map((attributeResult, index) => <HtmlAttributeResultDisplay attributeResult={attributeResult} key={index}/>)}

      {textContentResult && <li className={textContentResult.isSuccessful ? textColors.correct : textColors.inCorrect}>
        Das Element sollte den Textinhalt <code>{textContentResult.awaitedContent}</code> haben.
        Gefunden wurde <code>{textContentResult.maybeFoundContent}</code>.
      </li>
      }
    </ul>
  );
}
