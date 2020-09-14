import {Component, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'it4all-random-solve-buttons',
  template: `
    <div class="columns">
      <div class="column is-one-third-desktop">
        <button class="button is-link is-fullwidth" (click)="correctEmitter.emit()">Lösung testen</button>
      </div>
      <div class="column is-one-third-desktop">
        <button class="button is-primary is-fullwidth" (click)="nextEmitter.emit()">Nächste Aufgabe</button>
      </div>
      <div class="column is-one-third-desktop">
        <a class="button is-dark is-fullwidth" routerLink="..">Bearbeiten beenden</a>
      </div>
    </div>
  `
})
export class RandomSolveButtonsComponent {

  @Output() correctEmitter: EventEmitter<void> = new EventEmitter<void>();
  @Output() nextEmitter: EventEmitter<void> = new EventEmitter<void>();

}
