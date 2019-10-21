import {Component, Input, OnInit} from '@angular/core';
import {NaryHelpers, NaryNumberInput} from '../nary';

@Component({
  selector: 'it4all-nary-number-input',
  template: `
      <div class="field has-addons">
          <div class="control">
              <div class="button is-static">
                  <label for="{{naryNumberInput.fieldId}}">{{naryNumberInput.labelContent}}</label>
              </div>
          </div>
          <div class="control is-expanded">
              <input class="input has-text-right" id="{{naryNumberInput.fieldId}}" [value]="getValue()" [placeholder]="getPlaceholder()"
                     readonly>
          </div>
          <div class="control">
              <div class="button is-static">
                  <sub>{{naryNumberInput.numberingSystem.radix}}</sub>
              </div>
          </div>
      </div>`
})
export class NaryNumberInputComponent extends NaryHelpers implements OnInit {

  @Input() naryNumberInput: NaryNumberInput;

  constructor() {
    super();
  }

  ngOnInit() {
  }

  getValue(): string {
    if (this.naryNumberInput.decimalNumber) {
      return this.getSummandNary(this.naryNumberInput.decimalNumber, this.naryNumberInput.numberingSystem.radix);
    } else {
      return '';
    }
  }

  getPlaceholder(): string {
    return this.naryNumberInput.fieldPlaceholder || this.naryNumberInput.labelContent;
  }

}
