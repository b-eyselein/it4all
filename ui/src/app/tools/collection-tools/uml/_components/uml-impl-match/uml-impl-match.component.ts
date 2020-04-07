import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {UmlImplementationMatchFragment} from '../../uml-apollo-mutations.service';

@Component({
  selector: 'it4all-uml-impl-match',
  templateUrl: './uml-impl-match.component.html'
})
export class UmlImplMatchComponent implements OnChanges {

  @Input() implMatch: UmlImplementationMatchFragment;

  isCorrect = false;
  directionCorrect = false;

  ngOnChanges(changes: SimpleChanges): void {
    this.isCorrect = this.implMatch.matchType === 'SUCCESSFUL_MATCH';

    if (this.implMatch.userArg && this.implMatch.sampleArg) {
      this.directionCorrect = this.implMatch.userArg.subClass === this.implMatch.sampleArg.subClass;
    }
  }

  get subClass(): string {
    return this.implMatch.userArg ? this.implMatch.userArg.subClass : this.implMatch.sampleArg.subClass;
  }

  get superClass(): string {
    return this.implMatch.userArg ? this.implMatch.userArg.superClass : this.implMatch.sampleArg.superClass;
  }

  get wordForMatchType(): string {
    switch (this.implMatch.matchType) {
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

}
