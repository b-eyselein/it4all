import {Component, Input, OnInit} from '@angular/core';
import {XmlDocumentResultFragment} from '../../xml-apollo-mutations.service';

@Component({
  selector: 'it4all-xml-document-correction',
  template: `
    <p [ngClass]="isCorrect ? 'has-text-dark-success' : 'has-text-danger'">
      Die Korrektur war {{isCorrect ? '' : 'nicht'}} erfolgreich. Es
      wurden {{isCorrect ? 'keine' : result.errors.length}}
      Fehler gefunden{{isCorrect ? '.' : ':'}}
    </p>

    <div class="content" *ngIf="result.errors.length > 0">
      <ul>
        <li *ngFor="let err of result.errors"
            [ngClass]="err.errorType === 'WARNING' ? 'has-text-dark-warning' : 'has-text-danger'">
          <b>Fehler in Zeile {{err.line}}</b>:
          <code>{{err.errorMessage}}</code>
        </li>
      </ul>
    </div>
  `
})
export class XmlDocumentCorrectionComponent implements OnInit {

  @Input() result: XmlDocumentResultFragment;

  isCorrect = false;

  ngOnInit() {
    this.isCorrect = this.result.errors.length === 0;
  }

}
