import {Component, Input, OnInit} from '@angular/core';
import {ElementLineMatch} from '../../../../../_interfaces/graphql-types';

@Component({
  selector: 'it4all-xml-element-line-match',
  templateUrl: './xml-element-line-match.component.html'
})
export class XmlElementLineMatchComponent implements OnInit {

  @Input() match: ElementLineMatch;

  isCorrect = false;

  ngOnInit() {
    this.isCorrect = this.match.matchType === 'SUCCESSFUL_MATCH';
  }

  get matchWord(): string {
    switch (this.match.matchType) {
      case 'ONLY_SAMPLE':
        return 'fehlt!';
      case 'ONLY_USER':
        return 'ist falsch!';
      case 'SUCCESSFUL_MATCH':
        return 'ist korrekt.';
      default:
        return 'ist nicht komplett korrekt:';
    }
  }

}
