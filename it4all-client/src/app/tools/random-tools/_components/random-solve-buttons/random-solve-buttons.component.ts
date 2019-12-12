import {Component, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'it4all-random-solve-buttons',
  templateUrl: './random-solve-buttons.component.html'
})
export class RandomSolveButtonsComponent {

  @Output() correctEmitter: EventEmitter<void> = new EventEmitter<void>();
  @Output() nextEmitter: EventEmitter<void> = new EventEmitter<void>();

  constructor() {
  }

  correct(): void {
    this.correctEmitter.emit();
  }

  next(): void {
    this.nextEmitter.emit();
  }

}
