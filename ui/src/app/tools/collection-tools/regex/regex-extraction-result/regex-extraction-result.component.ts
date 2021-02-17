import {Component, Input, OnInit} from '@angular/core';
import {
  MatchType,
  RegexExtractionMatchFragment,
  RegexExtractionSingleResultFragment
} from "../../../../_services/apollo_services";

@Component({
  selector: 'it4all-regex-extraction-result',
  template: `
    <div class="notification">
      <p class="has-text-centered">Suche in: <code>{{extractionResult.base}}</code></p>

      <div class="columns is-multiline my-3">
        <div class="column is-half-desktop" *ngFor="let m of extractionResult.extractionMatchingResult.allMatches">

          <div class="notification" [ngClass]="isCorrect(m) ? 'is-success' : 'is-danger'">
            {{isCorrect(m) ? '&#10004;' : '&#10008;'}} Erwartet: <code>{{m.sampleArg}}</code>, bekommen:
            <code>{{m.userArg}}</code>
          </div>

        </div>

        <div class="column is-half-desktop"
             *ngFor="let mu of extractionResult.extractionMatchingResult.notMatchedForUser">
          <div class="notification is-danger">
            <code>{{mu}}</code> sollte nicht gefunden werden!
          </div>
        </div>

        <div class="column is-half-desktop"
             *ngFor="let ms of extractionResult.extractionMatchingResult.notMatchedForSample">
          <div class="notification is-danger">
            <code>{{ms}}</code> sollte gefunden werden!
          </div>
        </div>
      </div>
    </div>`
})
export class RegexExtractionResultComponent implements OnInit {

  @Input() extractionResult: RegexExtractionSingleResultFragment;

  isCorrect(m: RegexExtractionMatchFragment): boolean {
    return m.matchType === MatchType.SuccessfulMatch;
  }

  ngOnInit() {
    console.info(JSON.stringify(this.extractionResult));
  }

}
