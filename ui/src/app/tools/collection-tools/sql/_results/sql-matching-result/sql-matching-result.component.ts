import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {MatchType, StringMatchFragment, StringMatchingResultFragment} from '../../../../../_services/apollo_services';

@Component({
  selector: 'it4all-sql-matching-result',
  template: `
    <div [ngClass]="successful ? 'has-text-success' : 'has-text-danger'">
      ({{matchingResult.points.toFixed(1)}} / {{matchingResult.maxPoints.toFixed(1)}} P)
      Der Vergleich der {{matchName}} war {{successful ? "" : "nicht"}} erfolgreich.
    </div>
    <div class="content" *ngIf="!successful">
      <ul>
        <li *ngFor="let match of matchingResult.allMatches" [class]="getCssClassForMatchType(match.matchType)">
          Die Angabe {{matchSingularName}}
          <code>{{getArgDescription(match)}}</code> {{getTextForMatchType(match.matchType)}}.
        </li>
      </ul>
    </div>
  `
})
export class SqlMatchingResultComponent implements OnChanges {

  @Input() matchName: string;
  @Input() matchSingularName: string;

  @Input() matchingResult: StringMatchingResultFragment;

  successful: boolean;

  ngOnChanges(changes: SimpleChanges): void {
    this.successful = this.matchingResult.allMatches
      .every((m) => m.matchType === MatchType.SuccessfulMatch);
  }

  getCssClassForMatchType(matchType: MatchType): string {
    return (matchType === MatchType.SuccessfulMatch) ? 'has-text-success' : 'has-text-danger';
  }

  getArgDescription(match: StringMatchFragment): string {
    return match.matchType === MatchType.OnlySample ? match.sampleArg : match.userArg;
  }

  getTextForMatchType(matchType: MatchType): string {
    switch (matchType) {
      case MatchType.SuccessfulMatch:
        return 'ist korrekt';
      case MatchType.OnlySample:
        return 'fehlt';
      case MatchType.OnlyUser:
        return 'ist falsch';
      default:
        return 'ist nicht komplett richtig.';
    }
  }

}
