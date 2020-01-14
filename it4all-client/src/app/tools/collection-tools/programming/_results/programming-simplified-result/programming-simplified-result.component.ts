import {Component, Input, OnInit} from '@angular/core';
import {ProgSingleResult} from '../../my-programming-interfaces';

@Component({
  selector: 'it4all-programming-simplified-result',
  templateUrl: './programming-simplified-result.component.html',
})
export class ProgrammingSimplifiedResultComponent implements OnInit {

  @Input() result: ProgSingleResult;

  correct = false;

  constructor() {
  }

  ngOnInit(): void {
    this.correct = this.result.success === 'COMPLETE';
  }

}
