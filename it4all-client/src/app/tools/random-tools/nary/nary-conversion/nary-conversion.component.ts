import {Component, HostListener, OnInit} from '@angular/core';
import {NaryConversionToolPart} from '../../random-tools-list';
import {ToolPart} from '../../../../_interfaces/tool';
import {BINARY_SYSTEM, HEXADECIMAL_SYSTEM, NaryComponentBase, NaryReadOnlyNumberInput, NUMBERING_SYSTEMS, NumberingSystem} from '../nary';
import {randomInt} from '../../../../helpers';
import {Router} from '@angular/router';

@Component({templateUrl: './nary-conversion.component.html'})
export class NaryConversionComponent extends NaryComponentBase implements OnInit {

  toolPart: ToolPart = NaryConversionToolPart;

  // noinspection JSMismatchedCollectionQueryUpdate
  numberingSystems: NumberingSystem[] = NUMBERING_SYSTEMS;

  startSystem: NumberingSystem = BINARY_SYSTEM;
  targetSystem: NumberingSystem = HEXADECIMAL_SYSTEM;

  toConvertInput: NaryReadOnlyNumberInput = {
    decimalNumber: 0,
    numberingSystem: this.startSystem,
    fieldId: 'toConvert',
    labelContent: 'Startzahl:',
    maxValueForDigits: this.max
  };

  solutionString: string;

  checked = false;
  correct = false;

  constructor(private router: Router) {
    super();
  }

  ngOnInit(): void {
    this.update();
  }

  update(): void {
    this.toConvertInput.decimalNumber = randomInt(1, this.max);
    this.toConvertInput.numberingSystem = this.startSystem;
    this.toConvertInput.maxValueForDigits = this.max;

    this.solutionString = '';
    this.checked = false;
    this.correct = false;
  }

  checkSolution(): void {
    this.checked = true;

    const processedSolutionString: string = this.solutionString.replace(/\s+/g, '');

    const solution: number = parseInt(processedSolutionString, this.targetSystem.radix);

    this.correct = solution === this.toConvertInput.decimalNumber;
  }

  end(): void {
    this.router.navigate(['/randomTools', this.tool.id]);
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
