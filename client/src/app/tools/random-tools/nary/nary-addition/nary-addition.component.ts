import {Component, HostListener} from '@angular/core';
import {NaryAdditionToolPart, NaryTool} from '../../random-tools-list';
import {Tool, ToolPart} from '../../../../_interfaces/tool';
import {BINARY_SYSTEM, NaryReadOnlyNumberInput, NUMBERING_SYSTEMS, NumberingSystem} from '../nary';
import {randomInt} from '../../../../helpers';

@Component({
  templateUrl: './nary-addition.component.html',
  styles: [`
      #solution {
          direction: rtl;
          unicode-bidi: bidi-override
      }`
  ]
})
export class NaryAdditionComponent {

  tool: Tool = NaryTool;
  toolPart: ToolPart = NaryAdditionToolPart;

  // noinspection JSMismatchedCollectionQueryUpdate
  numberingSystems: NumberingSystem[] = NUMBERING_SYSTEMS;

  system: NumberingSystem = BINARY_SYSTEM;

  target = 0;
  firstSummandInput: NaryReadOnlyNumberInput = new NaryReadOnlyNumberInput(0, this.system, 'firstSummand', 'Summand 1:');
  secondSummandInput: NaryReadOnlyNumberInput = new NaryReadOnlyNumberInput(0, this.system, 'secondSummand', 'Summand 2:');

  checked = false;
  correct = false;
  solutionString = '';

  constructor() {
    this.update();
  }

  update(): void {
    this.target = randomInt(1, 256);

    const firstSummand = randomInt(1, this.target);

    this.firstSummandInput.decimalNumber = firstSummand;
    this.firstSummandInput.numberingSystem = this.system;

    this.secondSummandInput.decimalNumber = this.target - firstSummand;
    this.secondSummandInput.numberingSystem = this.system;

    this.checked = false;
    this.correct = false;
    this.solutionString = '';
  }

  checkSolution(): void {
    const reversedSolutionString: string = this.solutionString
      .replace(/\s/g, '')
      .split('').reverse().join('');

    const solution: number = parseInt(reversedSolutionString, this.system.radix);

    this.checked = true;

    this.correct = solution === this.target;
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
