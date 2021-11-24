import { ChangeEvent } from 'react';
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
    <div className="field">
      <label htmlFor="startSystem" className="label has-text-centered">{label}</label>
      <div className="control">
        <div className="select is-fullwidth">
          <select onChange={onChange} id="startSystem" defaultValue={system.radix}>
            {Object.values(numberingSystems).map((ns) => <option key={ns.radix} value={ns.radix}>{ns.radix} - {ns.name}</option>)}
          </select>
        </div>
      </div>
    </div>
  );
}
