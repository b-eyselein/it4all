import {ImplementationCorrectionResultFragment} from '../../../graphql';
import classNames from 'classnames';

interface IProps {
  result: ImplementationCorrectionResultFragment;
}

export function ImplementationResult({result}: IProps): JSX.Element {
  return (
    <div className={classNames('notification', result.successful ? 'is-success' : 'is-danger')}>
      <pre>{result.stderr.join('\n')}</pre>
    </div>
  );
}
