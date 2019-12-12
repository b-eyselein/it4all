import {Component, Input, OnInit} from '@angular/core';
import {AnalysisResult, MatchingResult} from '../../../basics';

@Component({selector: 'it4all-sql-matching-result', templateUrl: './sql-matching-result.component.html'})
export class SqlMatchingResultComponent implements OnInit {

  @Input() matchingResult: MatchingResult<string, AnalysisResult>;

  constructor() {
  }

  ngOnInit() {
  }

}
