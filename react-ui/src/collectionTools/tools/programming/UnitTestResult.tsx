import {UnitTestCorrectionResultFragment} from '../../../graphql';
import {textColors} from '../../../consts';

interface IProps {
  result: UnitTestCorrectionResultFragment;
}

export function UnitTestResult({result}: IProps): JSX.Element {

  const {testId, shouldFail, description, stderr, successful} = result;

  return (
    <li className="my-4">
      <span className={successful ? textColors.correct : textColors.inCorrect}>
        Der {testId}. Test war {successful ? '' : ' nicht'} erfolgreich.
        Der Test sollte {shouldFail ? '' : ' nicht'} fehlschlagen.
      </span>
      {!successful && <>
        <p className="p-2">Beschreibung: {description}</p>
        <pre className="p-2 rounded border border-slate-300">{stderr.join('\n')}</pre>
      </>}
    </li>
  );
}
