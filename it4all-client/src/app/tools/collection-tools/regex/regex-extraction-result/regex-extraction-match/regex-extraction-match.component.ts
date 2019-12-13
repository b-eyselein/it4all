import {Component, Input, OnInit} from '@angular/core';
import {AnalysisResult, Match} from '../../../../basics';
import {RegexExtractionMatch} from '../../regex-interfaces';

@Component({
  selector: 'it4all-regex-extraction-match',
  template: `
    <div class="notification" [ngClass]="isCorrect() ? 'is-success' : 'is-danger'">
      <span *ngIf="isCorrect()">{{isCorrect() ? '&#10004;' : '&#10008;'}}}</span>
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
