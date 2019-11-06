import {Component, HostListener, OnInit} from '@angular/core';
import {DECIMAL_SYSTEM, NaryComponentBase, NaryReadOnlyNumberInput} from '../nary';
import {NaryTwoConversionToolPart} from '../../random-tools-list';
import {ToolPart} from '../../../../_interfaces/tool';
import {randomInt} from '../../../../helpers';
import {Router} from '@angular/router';

@Component({templateUrl: './nary-two-conversion.component.html'})
export class NaryTwoConversionComponent extends NaryComponentBase implements OnInit {

  toolPart: ToolPart = NaryTwoConversionToolPart;

  withIntermediateSteps = true;

  toConvertInput: NaryReadOnlyNumberInput = {
    decimalNumber: 0,
    numberingSystem: DECIMAL_SYSTEM,
    fieldId: 'startNumber',
    labelContent: 'Startzahl:',
    maxValueForDigits: this.max
  };

  binaryAbsoluteString = '';
  invertedAbsoluteString = '';
  solutionString = '';

  checked = false;

  binaryAbsoluteCorrect = false;
  invertedAbsoluteCorrect = false;
  solutionCorrect = false;

  completelyCorrect = false;

  constructor(private router: Router) {
    super(128);
  }

  private static swapOnesAndZeros(str: string): string {
    return str
      .replace(/0/g, 'a')
      .replace(/1/g, '0')
      .replace(/a/g, '1');
  }

  ngOnInit(): void {
    this.update();
  }

  update(): void {
    this.toConvertInput.decimalNumber = randomInt(0, this.max);
    this.toConvertInput.maxValueForDigits = this.max;

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
