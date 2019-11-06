import {Component, Input} from '@angular/core';
import {NaryReadOnlyNumberInput} from '../../nary';

@Component({selector: 'it4all-nary-number-read-only-input', templateUrl: './nary-number-read-only-input.component.html'})
export class NaryNumberReadOnlyInputComponent {

  @Input() naryNumberInput: NaryReadOnlyNumberInput;

  constructor() {
  }

  getSummandNary(): string {
    const radix = this.naryNumberInput.numberingSystem.radix;
    const blockSize = radix === 2 ? 4 : 2;

    const minimalDigits: number = Math.log2(this.naryNumberInput.maxValueForDigits) / Math.log2(radix);
    const newDigitCount: number = Math.ceil(minimalDigits / blockSize) * blockSize;

    return this.naryNumberInput.decimalNumber
      .toString(radix)
      .padStart(newDigitCount, '0')
      .match(new RegExp(`.{1,${blockSize}}`, 'g')) // split in blocks
      .join(' ');
  }

}
