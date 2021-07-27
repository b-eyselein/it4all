import React from 'react';
import {NaryReadOnlyNumberInput} from './nary';


interface IProps {
  naryNumberInput: NaryReadOnlyNumberInput;
}

export function NaryNumberReadOnlyInputComponent({naryNumberInput}: IProps): JSX.Element {

  function getSummandNary(): string {
    const radix = naryNumberInput.numberingSystem.radix;
    const blockSize = radix === 2 ? 4 : 2;

    const minimalDigits: number = Math.log2(naryNumberInput.maxValueForDigits) / Math.log2(radix);
    const newDigitCount: number = Math.ceil(minimalDigits / blockSize) * blockSize;

    return naryNumberInput.decimalNumber
      .toString(radix)
      .padStart(newDigitCount, '0')
      .match(new RegExp(`.{1,${blockSize}}`, 'g'))! // split in blocks
      .join(' ');
  }

  return (
    <div className="field has-addons">
      <div className="control">
        <div className="button is-static">
          <label htmlFor="{naryNumberInput.fieldId}">{naryNumberInput.labelContent}</label>
        </div>
      </div>
      <div className="control is-expanded">
        <input className="input has-text-right" id={naryNumberInput.fieldId} value={getSummandNary()} placeholder={naryNumberInput.labelContent} readOnly/>
      </div>
      <div className="control">
        <div className="button is-static">
          <sub>{naryNumberInput.numberingSystem.radix}</sub>
        </div>
      </div>
    </div>

  );

}
