import {Component, Input, OnInit} from '@angular/core';
import {AnalysisResult, Match} from '../../../../basics';
import {RegexExtractionMatch} from '../../regex-exercise';

@Component({
  selector: 'it4all-regex-extraction-match',
  template: `
      <div class="notification" [class.is-success]="isCorrect()" [class.is-danger]="!isCorrect()">
          <span *ngIf="isCorrect()">&#10004;</span>
          <span *ngIf="!isCorrect()">&#10008;</span>
          <span *ngIf="match.sampleArg">Erwartet: <code>{{match.sampleArg?.content}}</code></span>,
          <span *ngIf="match.userArg">Bekommen: <code>{{match.userArg?.content}}</code></span>
      </div>
  `
})
export class RegexExtractionMatchComponent implements OnInit {

  @Input() match: Match<RegexExtractionMatch, AnalysisResult>;

  constructor() {
  }

  ngOnInit() {
  }

  isCorrect(): boolean {
    return this.match.matchType === 'SUCCESSFUL_MATCH';
  }

}
