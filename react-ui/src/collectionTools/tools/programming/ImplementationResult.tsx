import {ImplementationCorrectionResultFragment} from '../../../graphql';
import classNames from 'classnames';
import {textColors} from '../../../consts';

interface IProps {
  result: ImplementationCorrectionResultFragment;
}

export function ImplementationResult({result}: IProps): JSX.Element {
  return (
    <div className={classNames('p-2', 'rounded', 'border', 'border-slate-300', 'bg-slate-50', result.successful ? textColors.correct : textColors.inCorrect)}>
      <pre>{result.stderr.join('\n')}</pre>
    </div>
  );
}
