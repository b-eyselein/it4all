import {useTranslation} from 'react-i18next';

const minimalMax = 16;
const maximalMax = 4294967296; // Math.pow(2, 32)

interface IProps {
  max: number;
  update: (newMax: number) => void;
}

export function NaryLimits({max, update}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <div className="field">
      <label className="label has-text-centered">{t('maximalValue')}:</label>

      <div className="field has-addons">
        <div className="control">
          <button className="button" onClick={() => update(max / 2)} disabled={max === minimalMax}>/ 2</button>
        </div>
        <div className="control is-expanded">
          <input className="input has-text-centered" type="number" id="max" value={max} readOnly/>
        </div>
        <div className="control">
          <button className="button" onClick={() => update(max * 2)} disabled={max === maximalMax}>* 2</button>
        </div>
      </div>
    </div>
  );
}
