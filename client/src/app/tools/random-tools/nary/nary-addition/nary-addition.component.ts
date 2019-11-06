import {Component, HostListener, OnInit} from '@angular/core';
import {NaryAdditionToolPart} from '../../random-tools-list';
import {ToolPart} from '../../../../_interfaces/tool';
import {BINARY_SYSTEM, NaryComponentBase, NaryReadOnlyNumberInput, NUMBERING_SYSTEMS, NumberingSystem} from '../nary';
import {randomInt} from '../../../../helpers';
import {Router} from '@angular/router';

@Component({
  templateUrl: './nary-addition.component.html',
  styles: [`
      #solution {
          direction: rtl;
          unicode-bidi: bidi-override
      }`
  ]
})
export class NaryAdditionComponent extends NaryComponentBase implements OnInit {

  toolPart: ToolPart = NaryAdditionToolPart;

  // noinspection JSMismatchedCollectionQueryUpdate
  numberingSystems: NumberingSystem[] = NUMBERING_SYSTEMS;

  system: NumberingSystem = BINARY_SYSTEM;

  target = 0;

  firstSummandInput: NaryReadOnlyNumberInput = {
    decimalNumber: 0,
    numberingSystem: this.system,
    fieldId: 'firstSummand',
    labelContent: 'Summand 1:',
    maxValueForDigits: this.max
  };

  secondSummandInput: NaryReadOnlyNumberInput = {
    decimalNumber: 0,
    numberingSystem: this.system,
    fieldId: 'secondSummand',
    labelContent: 'Summand 2:',
    maxValueForDigits: this.max
  };

  checked = false;
  correct = false;
  solutionString = '';

  constructor(private router: Router) {
    super();
  }

  ngOnInit(): void {
    this.update();
  }

  update(): void {
    this.checked = false;
    this.correct = false;
    this.solutionString = '';

    this.target = randomInt(1, this.max);

    const firstSummand = randomInt(1, this.target);

    this.firstSummandInput.decimalNumber = firstSummand;
    this.firstSummandInput.numberingSystem = this.system;
    this.firstSummandInput.maxValueForDigits = this.max;

    this.secondSummandInput.decimalNumber = this.target - firstSummand;
    this.secondSummandInput.numberingSystem = this.system;
    this.secondSummandInput.maxValueForDigits = this.max;
  }

  checkSolution(): void {
    const reversedSolutionString: string = this.solutionString
      .replace(/\s/g, '')
      .split('').reverse().join('');

    const solution: number = parseInt(reversedSolutionString, this.system.radix);

    this.checked = true;

    this.correct = solution === this.target;
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
