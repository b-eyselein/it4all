import {Component, Input} from '@angular/core';
import {RegexExtractionSingleResultFragment} from '../../../../_services/apollo-mutation.service';

@Component({
  selector: 'it4all-regex-extraction-result',
  templateUrl: './regex-extraction-result.component.html',
})
export class RegexExtractionResultComponent {

  @Input() extractionResult: RegexExtractionSingleResultFragment;

}
