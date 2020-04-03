import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {UmlAssociation, UmlAssociationMatch, UmlMultiplicity} from "../../../../../_interfaces/graphql-types";

function printCardinality(c: UmlMultiplicity): string {
  switch (c) {
    case 'UNBOUND':
      return '*';
    case 'SINGLE':
      return '1';
    default:
      return 'ERROR!';
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

      this.cardsCorrect = this.cardinalitiesCorrect(this.assocMatch.userArg, this.assocMatch.sampleArg);
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


  cardinalitiesCorrect(userAssoc: UmlAssociation, sampleAssoc: UmlAssociation): boolean {
    return this.assocMatch.maybeAnalysisResult.multiplicitiesEqual;
  }

  get correctCardinalities(): string {
    if (this.assocMatch.userArg.firstEnd === this.assocMatch.sampleArg.firstEnd) {
      return printCardinality(this.assocMatch.sampleArg.firstMult) + ' : ' + printCardinality(this.assocMatch.sampleArg.secondMult);
    } else {
      return printCardinality(this.assocMatch.sampleArg.secondMult) + ' : ' + printCardinality(this.assocMatch.sampleArg.firstMult);
    }
  }

}
