import {Component, Input, OnInit} from '@angular/core';
import {IUmlAssociation, IUmlAssociationMatch, MatchType, UmlMultiplicity} from '../../uml-interfaces';

function printCardinality(c: UmlMultiplicity): string {
  return c === 'UNBOUND' ? '*' : '1';
}

@Component({
  selector: 'it4all-uml-assoc-match',
  templateUrl: './uml-assoc-match.component.html'
})
export class UmlAssocMatchComponent implements OnInit {

  @Input() assocMatch: IUmlAssociationMatch;

  isCorrect = false;

  assocTypeCorrect = false;
  cardsCorrect = false;

  gottenCardinalities = '';

  ngOnInit() {
    this.isCorrect = this.assocMatch.matchType === 'SUCCESSFUL_MATCH';

    if (this.assocMatch.userArg && this.assocMatch.sampleArg) {
      this.gottenCardinalities = printCardinality(this.assocMatch.userArg.firstMult) + ' : ' + printCardinality(this.assocMatch.sampleArg.secondMult);
      this.assocTypeCorrect = this.assocMatch.userArg.assocType === this.assocMatch.sampleArg.assocType;
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


  cardinalitiesCorrect(userAssoc: IUmlAssociation, sampleAssoc: IUmlAssociation): boolean {
    if (userAssoc.firstEnd === sampleAssoc.firstEnd) {
      return userAssoc.firstMult === sampleAssoc.firstMult && userAssoc.secondMult === sampleAssoc.secondMult;
    } else {
      return userAssoc.firstMult === sampleAssoc.secondMult && userAssoc.secondMult === sampleAssoc.firstMult;
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
