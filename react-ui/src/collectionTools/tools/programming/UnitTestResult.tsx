import {UnitTestCorrectionResultFragment} from '../../../graphql';

interface IProps {
  result: UnitTestCorrectionResultFragment;
}

export function UnitTestResult({result}: IProps): JSX.Element {

  const {testId, shouldFail, description, stderr, successful} = result;

  return (
    <li>
      <p className={successful ? 'has-text-success' : 'has-text-danger'}>
        Der {testId}. Test war {successful ? '' : ' nicht'} erfolgreich.
        Der Test sollte {shouldFail ? '' : ' nicht'} fehlschlagen.
      </p>
      {!successful && <>
        <p>Beschreibung: {description}</p>
        <pre>{stderr.join('\n')}</pre>
      </>}
    </li>
  );
}
