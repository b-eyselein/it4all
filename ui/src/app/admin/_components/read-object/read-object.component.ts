import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Saveable} from '../../../_interfaces/saveable';

@Component({
  selector: 'it4all-read-object',
  templateUrl: './read-object.component.html',
  styles: [`
    .loadedJson {
      max-height: 400px;
      overflow: auto;
    }`]
})
export class ReadObjectComponent<T extends Saveable> {

  @Input() name: string;
  @Input() loaded: T;

  @Output() save = new EventEmitter<void>();

}
