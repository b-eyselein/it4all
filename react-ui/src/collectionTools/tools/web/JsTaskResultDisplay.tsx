import {GradedJsTaskResultFragment, JsActionType, SuccessType} from '../../../graphql';
import {ElementSpecResultDisplay} from './ElementSpecResultDisplay';
import {useTranslation} from 'react-i18next';

interface IProps {
  jsResult: GradedJsTaskResultFragment;
}

export function JsTaskResultDisplay({jsResult}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const {id, success, points, maxPoints, gradedPostResults, gradedJsActionResult, gradedPreResults} = jsResult;

  console.info(success);

  if (success === SuccessType.Complete) {
    return <span className="has-text-success">Test {id} war erfolgreich.</span>;
  }

  const {actionPerformed, jsAction} = gradedJsActionResult;

  const {actionType, keysToSend} = jsAction;

  const actionDescription = actionType === JsActionType.Click
    ? <span>Klicke auf Element mit XPath Query <code>${jsAction.xpathQuery}</code></span>
    : <span>Sende Keys &quot;${keysToSend || ''}&quot; an Element mit XPath Query &quot;{jsAction.xpathQuery}&quot;</span>;


  return (
    <>
      <span className="has-text-danger">({points} / {maxPoints}) Test {id} war nicht erfolgreich</span>

      <ul>
        <li>
          {t('preConditions')}

          {gradedPreResults.map((r, index) => <ElementSpecResultDisplay key={index} elementSpecResult={r}/>)}
        </li>

        <li className={actionPerformed ? 'has-text-success' : 'has-text-danger'}>{actionDescription}</li>

        <li>
          {t('postConditions')}

          {gradedPostResults.map((r, index) => <ElementSpecResultDisplay key={index} elementSpecResult={r}/>)}
        </li>
      </ul>
    </>
  );
}
