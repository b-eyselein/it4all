import {GradedJsTaskResultFragment, JsActionType} from '../../../graphql';
import {ElementSpecResultDisplay} from './ElementSpecResultDisplay';
import {useTranslation} from 'react-i18next';
import {textColors} from '../../../consts';

interface IProps {
  jsResult: GradedJsTaskResultFragment;
}

export function JsTaskResultDisplay({jsResult}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const {id, /*success,*/ points, maxPoints, gradedPostResults, gradedJsActionResult, gradedPreResults} = jsResult;


  if (points === maxPoints) {
    return <span className={textColors.correct}>Test {id} war erfolgreich.</span>;
  }

  const {actionPerformed, jsAction} = gradedJsActionResult;

  const {actionType, keysToSend} = jsAction;

  const actionDescription = actionType === JsActionType.Click
    ? <span>Klicke auf Element mit XPath Query <code>${jsAction.xpathQuery}</code></span>
    : <span>Sende Keys &quot;${keysToSend || ''}&quot; an Element mit XPath Query &quot;{jsAction.xpathQuery}&quot;</span>;

  return (
    <>
      <span className={textColors.inCorrect}>({points} / {maxPoints}) Test {id} war nicht erfolgreich</span>

      <ul>
        <li>
          {t('preConditions')}

          {gradedPreResults.map((r, index) => <ElementSpecResultDisplay key={index} elementSpecResult={r}/>)}
        </li>

        <li className={actionPerformed ? textColors.correct : textColors.inCorrect}>{actionDescription}</li>

        <li>
          {t('postConditions')}

          {gradedPostResults.map((r, index) => <ElementSpecResultDisplay key={index} elementSpecResult={r}/>)}
        </li>
      </ul>
    </>
  );
}
