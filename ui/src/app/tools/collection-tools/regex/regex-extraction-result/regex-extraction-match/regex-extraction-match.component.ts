import {Component, Input} from '@angular/core';
import {IRegexMatchMatch} from '../../regex-interfaces';

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
export class RegexExtractionMatchComponent {

  @Input() match: IRegexMatchMatch;

  isCorrect(): boolean {
    return this.match.matchType === 'SUCCESSFUL_MATCH';
  }

}
