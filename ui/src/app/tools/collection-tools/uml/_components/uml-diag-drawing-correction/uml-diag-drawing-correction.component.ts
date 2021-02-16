import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {
  MatchType,
  UmlAssociationMatchFragment,
  UmlAssociationMatchingResultFragment,
  UmlImplementationMatch,
  UmlImplementationMatchingResultFragment,
  UmlResultFragment
} from '../../../../../_services/apollo_services';

@Component({
  selector: 'it4all-uml-diag-drawing-correction',
  templateUrl: './uml-diag-drawing-correction.component.html'
})
export class UmlDiagDrawingCorrectionComponent implements OnChanges {

  @Input() result: UmlResultFragment;

  assocResultSuccessful = false;
  implResultSuccessful = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.result.assocResult) {
      this.assocResultSuccessful = this.associationResultSuccessful(this.result.assocResult);
    }

    if (this.result.implResult) {
      this.implResultSuccessful = this.implementationResultSuccessful(this.result.implResult);
    }
  }

  assocMatchIsCorrect(m: UmlAssociationMatchFragment): boolean {
    return m.matchType === MatchType.SuccessfulMatch;
  }

  associationResultSuccessful(assocResult: UmlAssociationMatchingResultFragment): boolean {
    return assocResult.allMatches.every((x) => this.assocMatchIsCorrect(x));
  }


  implMatchIsCorrect(m: UmlImplementationMatch): boolean {
    return m.matchType === 'SUCCESSFUL_MATCH';
  }

  implementationResultSuccessful(implResult: UmlImplementationMatchingResultFragment): boolean {
    return implResult.allMatches.every(this.implMatchIsCorrect);
  }


}
