import {Component, Input} from '@angular/core';
import {RegexExtractionSingleResultFragment} from '../regex-apollo-mutations.service';

@Component({
  selector: 'it4all-regex-extraction-result',
  template: `
    <div class="notification">
      <p class="has-text-centered">
        Suche in: <code>{{extractionResult.base}}</code>
      </p>

      <br>

      <div class="columns is-multiline">
        <div class="column is-half-desktop" *ngFor="let match of extractionResult.extractionMatchingResult.allMatches">
          <it4all-regex-extraction-match [match]="match"></it4all-regex-extraction-match>
        </div>
      </div>
    </div>`
})
export class RegexExtractionResultComponent {

  @Input() extractionResult: RegexExtractionSingleResultFragment;

}
