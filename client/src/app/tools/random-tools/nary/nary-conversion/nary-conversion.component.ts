import {Component, HostListener} from '@angular/core';
import {NaryConversionToolPart, NaryTool} from '../../random-tools-list';
import {Tool, ToolPart} from '../../../../_interfaces/tool';
import {BINARY_SYSTEM, HEXADECIMAL_SYSTEM, NaryHelpers, NaryNumberInput, NUMBERING_SYSTEMS, NumberingSystem} from '../nary';
import {randomInt} from '../../../../helpers';

@Component({templateUrl: './nary-conversion.component.html'})
export class NaryConversionComponent extends NaryHelpers {

   tool: Tool = NaryTool;
   toolPart: ToolPart = NaryConversionToolPart;

  // noinspection JSMismatchedCollectionQueryUpdate
   numberingSystems: NumberingSystem[] = NUMBERING_SYSTEMS;

   startSystem: NumberingSystem = BINARY_SYSTEM;
   targetSystem: NumberingSystem = HEXADECIMAL_SYSTEM;

   toConvertInput: NaryNumberInput = new NaryNumberInput(this.startSystem, 'toConvert', 'Startzahl:', null, 0, true);

   solutionString: string;

   checked = false;
   correct = false;

  constructor() {
    super();
    this.update();
  }

  update(): void {
    this.toConvertInput.decimalNumber = randomInt(1, 256);
    this.toConvertInput.numberingSystem = this.startSystem;

    this.solutionString = '';
    this.checked = false;
    this.correct = false;
  }

  checkSolution(): void {
    this.checked = true;

    const solution: number = parseInt(this.solutionString.trim(), this.targetSystem.radix);

    this.correct = solution === this.toConvertInput.decimalNumber;
  }

  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      if (this.correct) {
        this.update();
      } else {
        this.checkSolution();
      }
    }
  }

}
