import {Component, Input} from '@angular/core';

@Component({
  selector: 'it4all-solution-saved',
  template: `
      <div class="notification is-success" *ngIf="saved; else notSavedBlock">
          &#10004; Ihre Lösung wurde gespeichert.
      </div>
      <ng-template #notSavedBlock>
          <div class="notification is-danger">
              &#10008; Ihre Lösung konnte nicht gespeichert werden!
          </div>
      </ng-template>`,
})
export class SolutionSavedComponent {

  @Input() saved: boolean;

  constructor() {
  }

}
