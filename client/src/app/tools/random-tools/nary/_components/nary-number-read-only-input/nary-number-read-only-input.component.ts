import {Component, Input} from '@angular/core';
import {NaryReadOnlyNumberInput} from '../../nary';

@Component({selector: 'it4all-nary-number-read-only-input', templateUrl: './nary-number-read-only-input.component.html'})
export class NaryNumberReadOnlyInputComponent {

  @Input() naryNumberInput: NaryReadOnlyNumberInput;

  constructor() {
  }

  private getSummandNary(): string {
    const radix = this.naryNumberInput.numberingSystem.radix;

    let naryString: string = this.naryNumberInput.decimalNumber.toString(radix);

    if (radix === 2) {
      naryString = naryString
        .padStart(8, '0')
        .match(/.{1,4}/g) // split in block of size 4
        .join(' ');
    }

    return naryString;
  }

  getPlaceholder(): string {
    return this.naryNumberInput.fieldPlaceholder || this.naryNumberInput.labelContent;
  }

}
