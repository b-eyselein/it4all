import {Component, Input, OnInit} from '@angular/core';
import {IXmlError} from '../../xml-interfaces';

@Component({
  selector: 'it4all-xml-document-correction',
  templateUrl: './xml-document-correction.component.html'
})
export class XmlDocumentCorrectionComponent implements OnInit {

  @Input() result: IXmlError[];

  isCorrect = false;

  ngOnInit() {
    this.isCorrect = this.result.length === 0;
  }

}
