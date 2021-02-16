import {Component, Input} from '@angular/core';
import {
  MatchType,
  UmlAssociationMatchFragment,
  UmlAssociationMatchingResultFragment,
  UmlMultiplicity
} from "../../../../../_services/apollo_services";

function printCardinality(c: UmlMultiplicity): string {
  return c === UmlMultiplicity.Unbound ? '*' : '1';
}

@Component({
  selector: 'it4all-uml-assoc-match-result',
  template: `
    <ul>
      <li *ngFor="let m of assocResult.allMatches">
        <div [ngClass]="isCorrect(m) ? 'has-text-dark-success' : 'has-text-danger'">
          Die Assoziation von <code>{{m.userArg.firstEnd}}</code> nach
          <code>{{m.userArg.secondEnd}}</code> ist {{isCorrect(m) ? '' : 'nicht'}} korrekt.

          <div *ngIf="!isCorrect" class="content">
            <ul>
              <li [ngClass]="m.analysisResult.assocTypeEqual ? 'has-text-dark-success' : 'has-text-danger'">
                Der Typ der Assoziation <code>{{m.userArg.assocType}}</code>
                war {{m.analysisResult.assocTypeEqual ? '' : 'nicht'}} korrekt.
              </li>
              <li
                [ngClass]="m.analysisResult.multiplicitiesEqual ? 'has-text-dark-success' : 'has-text-danger'">
                Die Kardinalitäten der Assoziation <code>{{gottenMultiplicities(m)}}</code>
                waren {{m.analysisResult.multiplicitiesEqual ? '' : 'nicht'}}
                korrekt.
                <ng-container *ngIf="!m.analysisResult.multiplicitiesEqual">Erwartet
                  wurde {{correctCardinalities}}</ng-container>
              </li>
            </ul>
          </div>
        </div>
      </li>

      <li *ngFor="let mu of assocResult.notMatchedForUser" class="has-text-danger">
        Die Assoziation von <code>{{mu.firstEnd}}</code> nach <code>{{mu.secondEnd}}</code> ist falsch.
      </li>

      <li *ngFor="let ms of assocResult.notMatchedForSample" class="has-text-danger">
        Die Assoziation von <code>{{ms.firstEnd}}</code> nach <code>{{ms.secondEnd}}</code> fehlt.
      </li>
    </ul>
  `
})
export class UmlAssocMatchResultComponent {

  @Input() assocResult: UmlAssociationMatchingResultFragment;

  isCorrect(m: UmlAssociationMatchFragment): boolean {
    return m.matchType === MatchType.SuccessfulMatch;
  }

  gottenMultiplicities(m: UmlAssociationMatchFragment): string {
    return `${printCardinality(m.userArg.firstMult)} : ${printCardinality(m.userArg.secondMult)}`;
  }

  correctCardinalities(m: UmlAssociationMatchFragment): string {
    if (m.userArg.firstEnd === m.sampleArg.firstEnd) {
      return printCardinality(m.sampleArg.firstMult) + ' : ' + printCardinality(m.sampleArg.secondMult);
    } else {
      return printCardinality(m.sampleArg.secondMult) + ' : ' + printCardinality(m.sampleArg.firstMult);
    }
  }


}
