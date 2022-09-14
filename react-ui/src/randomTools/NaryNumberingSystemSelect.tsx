import {ChangeEvent} from 'react';
import {NumberingSystem, numberingSystems} from './nary';

interface IProps {
  label: string;
  system: NumberingSystem;
  update: (newSystem: NumberingSystem) => void;
}

export function NaryNumberingSystemSelect({label, system, update}: IProps): JSX.Element {

  function onChange(event: ChangeEvent<HTMLSelectElement>): void {
    update(numberingSystems[parseInt(event.target.value)]);
  }

  return (
    <div>
      <label htmlFor="startSystem" className="font-bold block text-center">{label}:</label>

      <select onChange={onChange} id="startSystem" defaultValue={system.radix} className="p-2 rounded border border-slate-400 bg-white w-full">
        {Object.values(numberingSystems).map((ns) => <option key={ns.radix} value={ns.radix}>{ns.radix} - {ns.name}</option>)}
      </select>
    </div>
  );
}
