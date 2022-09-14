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
    <div>
      <label htmlFor="max" className="font-bold block text-center">{t('maximalValue')}:</label>

      <div className="flex">
        <button type="button" className="p-2 rounded-l border-l border-y border-slate-400" onClick={() => update(max / 2)} disabled={max === minimalMax}>
          / 2
        </button>
        <input type="number" className="flex-grow p-2 border border-slate-400 text-center" id="max" value={max} readOnly/>
        <button type="button" className="p-2 rounded-r border-r border-y border-slate-400" onClick={() => update(max * 2)} disabled={max === maximalMax}>
          * 2
        </button>
      </div>
    </div>
  );
}
