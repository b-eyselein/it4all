import {Component, Input, OnInit} from '@angular/core';
import {XmlElementLineMatchFragment} from '../../xml-apollo-mutations.service';
import {MatchType} from '../../xml-apollo-mutations.service';

@Component({
  selector: 'it4all-xml-element-line-match',
  templateUrl: './xml-element-line-match.component.html'
})
export class XmlElementLineMatchComponent implements OnInit {

  @Input() match: XmlElementLineMatchFragment;

  isCorrect = false;

  ngOnInit() {
    this.isCorrect = this.match.matchType === MatchType.SuccessfulMatch;
  }

  get matchWord(): string {
    switch (this.match.matchType) {
      case MatchType.OnlySample:
        return 'fehlt!';
      case MatchType.OnlyUser:
        return 'ist falsch!';
      case MatchType.SuccessfulMatch:
        return 'ist korrekt.';
      case MatchType.PartialMatch:
      case MatchType.UnsuccessfulMatch:
        return 'ist nicht komplett korrekt:';
    }
  }

}
