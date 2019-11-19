import {AfterContentInit, Component, ContentChildren, QueryList} from '@angular/core';
import {TabComponent} from '../tab/tab.component';

@Component({
  selector: 'it4all-tabs',
  template: `
      <div class="tabs is-centered">
          <ul>
              <li *ngFor="let tab of tabs" (click)="selectTab(tab)" [class.is-active]="tab.active">
                  <a>{{tab.title}}</a>
              </li>
          </ul>
      </div>
      <ng-content></ng-content>`,
})
export class TabsComponent implements AfterContentInit {

  @ContentChildren(TabComponent) tabs: QueryList<TabComponent>;

  constructor() {
  }

  ngAfterContentInit() {
    const activeTabs = this.tabs.filter((t) => t.active);

    if (this.tabs.length > 0 && activeTabs.length === 0) {
      this.selectTab(this.tabs.first);
    }
  }

  selectTab(tab: TabComponent): void {
    this.tabs.toArray().forEach((t) => t.active = false);

    tab.active = true;
  }

}
