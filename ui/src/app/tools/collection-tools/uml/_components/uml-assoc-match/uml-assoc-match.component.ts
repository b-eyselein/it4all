import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {UmlAssociationMatch, UmlMultiplicity} from '../../../../../_services/apollo_services';

function printCardinality(c: UmlMultiplicity): string {
  switch (c) {
    case UmlMultiplicity.Unbound:
      return '*';
    case UmlMultiplicity.Single:
      return '1';
  }
}

@Component({
  selector: 'it4all-uml-assoc-match',
  templateUrl: './uml-assoc-match.component.html'
})
export class UmlAssocMatchComponent implements OnChanges {

  @Input() assocMatch: UmlAssociationMatch;

  isCorrect = false;

  assocTypeCorrect = false;
  cardsCorrect = false;

  gottenCardinalities = '';

  ngOnChanges(changes: SimpleChanges): void {
    this.isCorrect = this.assocMatch.matchType === 'SUCCESSFUL_MATCH';

    if (this.assocMatch.userArg && this.assocMatch.sampleArg) {
      const firstMult = printCardinality(this.assocMatch.userArg.firstMult);
      const secondMult = printCardinality(this.assocMatch.userArg.secondMult);

      this.gottenCardinalities = firstMult + ' : ' + secondMult;

      this.assocTypeCorrect = this.assocMatch.maybeAnalysisResult.assocTypeEqual;

      this.cardsCorrect = this.assocMatch.maybeAnalysisResult.multiplicitiesEqual;
    }
  }

  get firstEnd(): string {
    return this.assocMatch.userArg ? this.assocMatch.userArg.firstEnd : this.assocMatch.sampleArg.firstEnd;
  }

  get secondEnd(): string {
    return this.assocMatch.userArg ? this.assocMatch.userArg.secondEnd : this.assocMatch.sampleArg.secondEnd;
  }

  get wordForMatchType(): string {
    switch (this.assocMatch.matchType) {
      case 'SUCCESSFUL_MATCH':
        return 'ist korrekt.';
      case 'PARTIAL_MATCH':
      case 'UNSUCCESSFUL_MATCH':
        return 'ist nicht korrekt.';
      case 'ONLY_SAMPLE':
        return 'fehlt.';
      case 'ONLY_USER':
        return 'ist falsch.';
    }
  }

  get correctCardinalities(): string {
    if (this.assocMatch.userArg.firstEnd === this.assocMatch.sampleArg.firstEnd) {
      return printCardinality(this.assocMatch.sampleArg.firstMult) + ' : ' + printCardinality(this.assocMatch.sampleArg.secondMult);
    } else {
      return printCardinality(this.assocMatch.sampleArg.secondMult) + ' : ' + printCardinality(this.assocMatch.sampleArg.firstMult);
    }
  }

}
