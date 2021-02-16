import {Component, Input} from '@angular/core';
import {MatchType, RegexExtractionMatchFragment} from "../../../../../_services/apollo_services";

@Component({
  selector: 'it4all-regex-extraction-match',
  template: `
    <div class="notification" [ngClass]="isCorrect() ? 'is-success' : 'is-danger'">
      <span *ngIf="isCorrect()">{{isCorrect() ? '&#10004;' : '&#10008;'}}</span>
      <span *ngIf="match.sampleArg">Erwartet: <code>{{match.sampleArg}}</code></span>,
      <span *ngIf="match.userArg">Bekommen: <code>{{match.userArg}}</code></span>
    </div>
  `
})
export class RegexExtractionMatchComponent {

  @Input() match: RegexExtractionMatchFragment;

  isCorrect(): boolean {
    return this.match.matchType === MatchType.SuccessfulMatch;
  }

}
