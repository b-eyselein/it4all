import {Component, Input, OnInit} from '@angular/core';
import {RegexExtractionResult} from '../regex-exercise';

@Component({
  selector: 'it4all-regex-extraction-result',
  templateUrl: './regex-extraction-result.component.html',
})
export class RegexExtractionResultComponent implements OnInit {

  @Input() extractionResult: RegexExtractionResult;

  constructor() {
  }

  ngOnInit() {
  }

}
