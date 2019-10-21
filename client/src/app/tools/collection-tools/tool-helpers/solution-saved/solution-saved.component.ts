import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-solution-saved',
  template: `
      <div class="notification has-text-success" *ngIf="saved; else notSavedBlock">Ihre Lösung wurde
          gespeichert.
      </div>
      <ng-template #notSavedBlock>
          <div class="notification has-text-danger">Ihre Lösung konnte nicht gespeichert werden!</div>
      </ng-template>`,
})
export class SolutionSavedComponent implements OnInit {

  @Input() saved: boolean;

  constructor() {
  }

  ngOnInit() {
  }

}
