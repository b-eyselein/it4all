import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {
  IAssociationMatchingResult,
  IImplementationMatchingResult,
  IUmlAssociationMatch,
  IUmlCompleteResult,
  IUmlImplementationMatch
} from '../../uml-interfaces';

@Component({
  selector: 'it4all-uml-diag-drawing-correction',
  templateUrl: './uml-diag-drawing-correction.component.html'
})
export class UmlDiagDrawingCorrectionComponent implements OnChanges {

  @Input() result: IUmlCompleteResult;

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


  assocMatchIsCorrect(m: IUmlAssociationMatch): boolean {
    return m.matchType === 'SUCCESSFUL_MATCH';
  }

  associationResultSuccessful(assocResult: IAssociationMatchingResult): boolean {
    return assocResult.allMatches.every(this.assocMatchIsCorrect);
  }


  implMatchIsCorrect(m: IUmlImplementationMatch): boolean {
    return m.matchType === 'SUCCESSFUL_MATCH';
  }

  implementationResultSuccessful(implResult: IImplementationMatchingResult): boolean {
    return implResult.allMatches.every(this.implMatchIsCorrect);
  }


}
