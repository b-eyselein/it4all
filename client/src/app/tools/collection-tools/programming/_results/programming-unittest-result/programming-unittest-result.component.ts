import {Component, Input, OnInit} from '@angular/core';
import {UnitTestCorrectionResult} from '../../programming';

@Component({
  selector: 'it4all-programming-unittest-result',
  templateUrl: './programming-unittest-result.component.html'
})
export class ProgrammingUnittestResultComponent implements OnInit {

  @Input() result: UnitTestCorrectionResult;

  correct = false;

  constructor() {
  }

  ngOnInit() {
    this.correct = this.result.successful;
  }

}
