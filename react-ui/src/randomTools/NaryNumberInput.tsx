import classNames from 'classnames';

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

export function NaryNumberInput({labelContent, initialValue, checked, correct, radix, update, rtl, autoFocus, disabled}: IProps): JSX.Element {
  return (
    <>
      <div className="field">
        <div className="field has-addons">
          <div className="control">
            <div className="button is-static">{labelContent}:</div>
          </div>
          <div className="control is-expanded">
            <input type="text" placeholder={labelContent} defaultValue={initialValue}
                   style={rtl ? {direction: 'rtl', unicodeBidi: 'bidi-override'} : {}}
                   className={classNames('input', 'has-text-right', {'is-success': checked && correct, 'is-danger': checked && !correct})}
                   onChange={(event) => update(event.target.value)}
                   autoFocus={autoFocus} autoComplete="off" disabled={disabled}/>
          </div>
          <div className="control">
            <div className=" button is-static">
              <sub>{radix}</sub>
            </div>
          </div>
        </div>
      </div>

      {checked && <div className={classNames('notification', 'has-text-centered', correct ? 'is-success' : 'is-danger')}>
        {correct ? <span>&#10004;</span> : <span>&#10008;</span>} Die Lösung ist {correct ? '' : ' nicht'} korrekt.
      </div>}
    </>
  );
}
