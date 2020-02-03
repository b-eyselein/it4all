import {Component, Input} from '@angular/core';

@Component({
  selector: 'it4all-string-sample-sol',
  template: `
    <div class="notification is-light-grey">
      <pre>{{sample}}</pre>
    </div>`
})
export class StringSampleSolComponent {

  @Input() sample: string;

}
