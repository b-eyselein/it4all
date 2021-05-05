import {Component, Input} from '@angular/core';
import {NaryReadOnlyNumberInput} from '../../nary';

@Component({
  selector: 'it4all-nary-number-read-only-input',
  template: `
    <div class="field has-addons">
      <div class="control">
        <div class="button is-static">
          <label for="{{naryNumberInput.fieldId}}">{{naryNumberInput.labelContent}}</label>
        </div>
      </div>
      <div class="control is-expanded">
        <input class="input has-text-right" id="{{naryNumberInput.fieldId}}" [value]="getSummandNary()"
               [placeholder]="naryNumberInput.labelContent" readonly>
      </div>
      <div class="control">
        <div class="button is-static">
          <sub>{{naryNumberInput.numberingSystem.radix}}</sub>
        </div>
      </div>
    </div>

  `
})
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
