import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Saveable} from '../../../_interfaces/saveable';

@Component({
  selector: 'it4all-read-object',
  template: `
    <ng-container *ngIf="!loaded.saved; else exerciseSavedBlock">
      <div class="card">
        <header class="card-header">
          <p class="card-header-title">{{loaded.title}}</p>
        </header>
        <div class="card-content">
          <pre class="loadedJson">{{loaded.value | json}}</pre>
        </div>
        <footer class="card-footer">
          <a class="card-footer-item" (click)="save.emit()">{{name}} speichern</a>
        </footer>
      </div>
    </ng-container>

    <ng-template #exerciseSavedBlock>
      <div class="notification is-success has-text-centered">{{name}} {{loaded.title}} wurde gespeichert.</div>
    </ng-template>
  `,
  styles: [`
    .loadedJson {
      max-height: 400px;
      overflow: auto;
    }`
  ]
})
export class ReadObjectComponent<T> {

  @Input() name: string;
  @Input() loaded: Saveable<T>;

  @Output() save = new EventEmitter<void>();

}
