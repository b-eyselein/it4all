import {Component, Input} from '@angular/core';

@Component({
  selector: 'it4all-tab',
  template: `
      <div [hidden]="!active">
          <ng-content></ng-content>
      </div>`,
})
export class TabComponent {

  @Input() title: string;
  @Input() active = false;
  @Input() selectable = true;

}
