import {Component, OnInit} from '@angular/core';
import {BINARY_SYSTEM, DECIMAL_SYSTEM, NaryHelpers, NaryNumberInput} from '../nary';
import {NaryTool, NaryTwoConversionToolPart} from '../../random-tools-list';
import {Tool, ToolPart} from '../../../../_interfaces/tool';
import {randomInt} from '../../../../helpers';

@Component({
  // selector: 'app-two-conversion',
  templateUrl: './nary-two-conversion.component.html'
})
export class NaryTwoConversionComponent extends NaryHelpers implements OnInit {

   tool: Tool = NaryTool;
   toolPart: ToolPart = NaryTwoConversionToolPart;

   withIntermediateSteps = true;
   toConvertInput: NaryNumberInput;

   binaryAbsolute: NaryNumberInput;

  constructor() {
    super();
    this.toConvertInput = new NaryNumberInput(DECIMAL_SYSTEM, 'startNumber', 'Startzahl:', null, 0, true);
    this.binaryAbsolute = new NaryNumberInput(BINARY_SYSTEM, 'binaryAbsolute', 'Binärdarstellung:', 'Binärdarstellung');
  }

  ngOnInit() {
    this.update();
  }

  update(): void {
    this.toConvertInput.decimalNumber = randomInt(0, 256);
    this.binaryAbsolute.decimalNumber = undefined;
  }

  checkSolution(): void {

  }

}
