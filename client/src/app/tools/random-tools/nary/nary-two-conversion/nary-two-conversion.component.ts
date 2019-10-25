import {Component, HostListener} from '@angular/core';
import {DECIMAL_SYSTEM, NaryReadOnlyNumberInput} from '../nary';
import {NaryTool, NaryTwoConversionToolPart} from '../../random-tools-list';
import {Tool, ToolPart} from '../../../../_interfaces/tool';
import {randomInt} from '../../../../helpers';
import {Router} from '@angular/router';

@Component({templateUrl: './nary-two-conversion.component.html'})
export class NaryTwoConversionComponent {

  tool: Tool = NaryTool;
  toolPart: ToolPart = NaryTwoConversionToolPart;

  withIntermediateSteps = true;

  toConvertInput: NaryReadOnlyNumberInput;

  binaryAbsoluteString = '';
  invertedAbsoluteString = '';
  solutionString = '';

  checked = false;

  binaryAbsoluteCorrect = false;
  invertedAbsoluteCorrect = false;
  solutionCorrect = false;

  completelyCorrect = false;

  constructor(private router: Router) {
    this.toConvertInput = new NaryReadOnlyNumberInput(0, DECIMAL_SYSTEM, 'startNumber', 'Startzahl:');

    this.update();
  }

  private static swapOnesAndZeros(str: string): string {
    return str
      .replace(/0/g, 'a')
      .replace(/1/g, '0')
      .replace(/a/g, '1');
  }

  update(): void {
    this.toConvertInput.decimalNumber = randomInt(0, 256);

    this.checked = false;

    this.binaryAbsoluteString = '';
    this.invertedAbsoluteString = '';
    this.solutionString = '';
  }

  checkSolution(): void {
    this.checked = true;

    const absoluteToConvert = Math.abs(this.toConvertInput.decimalNumber);

    const binAbsStr: string = this.binaryAbsoluteString.replace(/\s+/g, '');
    this.binaryAbsoluteCorrect = binAbsStr.length === 8 && parseInt(binAbsStr, 2) === absoluteToConvert;

    const invAbsStr: string = this.invertedAbsoluteString.replace(/\s+/g, '');
    const awaitedInvertedAbs: string = NaryTwoConversionComponent.swapOnesAndZeros(absoluteToConvert.toString(2).padStart(8, '0'));
    this.invertedAbsoluteCorrect = invAbsStr.length === 8 && awaitedInvertedAbs === invAbsStr;

    const solStr: string = this.solutionString.replace(/\s+/g, '');
    const awaitedSolution: string = (parseInt(awaitedInvertedAbs, 2) + 1).toString(2).padStart(8, '0');

    this.solutionCorrect = solStr.length === 8 && awaitedSolution === solStr;
  }

  end(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['/randomTools', this.tool.id]);
  }

  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      if (this.completelyCorrect) {
        this.update();
      } else {
        this.checkSolution();
      }
    }
  }

}
