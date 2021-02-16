import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {
  MatchType,
  UmlAssociationMatchFragment,
  UmlAssociationMatchingResultFragment,
  UmlResultFragment
} from '../../../../../_services/apollo_services';

@Component({
  selector: 'it4all-uml-diag-drawing-correction',
  template: `
    <!-- <it4all-points-notification [points]="result.points" [maxPoints]="result.maxPoints"></it4all-points-notification> -->

    <ng-container *ngIf="result.classResult">
      <!-- TODO: class result? -->
    </ng-container>

    <br>

    <ng-container *ngIf="result.assocResult">
      <h3 class="subtitle is-5" [ngClass]="assocResultSuccessful ? 'has-text-dark-success' : 'has-text-danger'">
        Der Vergleich der Assoziationen war {{assocResultSuccessful ? '' : 'nicht'}} erfolgreich.
      </h3>

      <div class="content" *ngIf="!assocResultSuccessful">
        <it4all-uml-assoc-match-result [assocResult]="result.assocResult"></it4all-uml-assoc-match-result>
      </div>
    </ng-container>

    <ng-container *ngIf="result.implResult">
      <it4all-uml-impl-result [implResult]="result.implResult"></it4all-uml-impl-result>
    </ng-container>
  `
})
export class UmlDiagDrawingCorrectionComponent implements OnChanges {

  @Input() result: UmlResultFragment;

  assocResultSuccessful = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.result.assocResult) {
      this.assocResultSuccessful = this.associationResultSuccessful(this.result.assocResult);
    }
  }

  assocMatchIsCorrect(m: UmlAssociationMatchFragment): boolean {
    return m.matchType === MatchType.SuccessfulMatch;
  }

  associationResultSuccessful(assocResult: UmlAssociationMatchingResultFragment): boolean {
    return assocResult.allMatches.every((x) => this.assocMatchIsCorrect(x));
  }


}
