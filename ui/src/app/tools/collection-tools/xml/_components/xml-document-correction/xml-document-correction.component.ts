import {Component, Input, OnInit} from '@angular/core';
import {XmlErrorFragment} from '../../xml-apollo-mutations.service';

@Component({
  selector: 'it4all-xml-document-correction',
  template: `
    <p [ngClass]="isCorrect ? 'has-text-dark-success' : 'has-text-danger'">
      Die Korrektur war {{isCorrect ? '' : 'nicht'}} erfolgreich. Es wurden {{isCorrect ? 'keine' : result.length}}
      Fehler
      gefunden{{isCorrect ? '.' : ':'}}
    </p>

    <div class="content" *ngIf="result.length > 0">
      <ul>
        <li *ngFor="let err of result"
            [ngClass]="err.errorType === 'WARNING' ? 'has-text-dark-warning' : 'has-text-danger'">
          <b>Fehler in Zeile {{err.line}}</b>:
          <code>{{err.errorMessage}}</code>
        </li>
      </ul>
    </div>`
})
export class XmlDocumentCorrectionComponent implements OnInit {

  @Input() result: XmlErrorFragment[];

  isCorrect = false;

  ngOnInit() {
    this.isCorrect = this.result.length === 0;
  }

}
