import classNames from 'classnames';
import {CSSProperties} from 'react';
import {bgColors} from '../consts';
import {useTranslation} from 'react-i18next';

interface IProps {
  labelContent: string;
  initialValue: string;
  checked: boolean;
  correct: boolean;
  radix: number;
  update: (newValue: string) => void;
  rtl?: boolean;
  autoFocus?: boolean;
  disabled?: boolean;
}

function reverseString(str: string): string {
  return str.split('').reverse().join('');
}

export function NaryNumberInput({labelContent, initialValue, checked, correct, radix, update, rtl, autoFocus, disabled}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const classes = classNames('flex-grow', 'p-2', 'border', 'border-slate-400', 'text-right', 'font-mono', {
    'is-success': checked && correct,
    'is-danger': checked && !correct,
    'bg-gray-200': disabled
  });

  const style: CSSProperties = rtl ? {direction: 'rtl', unicodeBidi: 'bidi-override'} : {};

  return (
    <div className="my-4">
      <div className="flex">
        <div className="p-2 rounded-l border-l border-y border-slate-400 bg-slate-100 is-static">{labelContent}:</div>
        <input type="text" placeholder={rtl ? reverseString(labelContent) : labelContent} defaultValue={initialValue} style={style}
               className={classes} onChange={(event) => update(event.target.value)} autoFocus={autoFocus} autoComplete="off" disabled={disabled}/>
        <div className="p-2 rounded-r border-r border-y border-slate-400 bg-slate-100">
          <sub>{radix}</sub>
        </div>
      </div>

      {checked && <div className={classNames('my-4', 'p-2', 'rounded', 'text-center', 'text-white', correct ? bgColors.correct : bgColors.inCorrect)}>
        {correct ? <span>&#10004; {t('solutionCorrect')}.</span> : <span>&#10008; {t('solutionNotCorrect')}.</span>}
      </div>}
    </div>
  );
}
