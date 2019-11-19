import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TabComponent} from './tab/tab.component';
import {TabsComponent} from './tabs/tabs.component';
import {PointsNotificationComponent} from './points-notification/points-notification.component';
import {SolutionSavedComponent} from './solution-saved/solution-saved.component';


@NgModule({
  declarations: [
    TabComponent, TabsComponent,
    PointsNotificationComponent,
    SolutionSavedComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    TabComponent, TabsComponent,
    PointsNotificationComponent,
    SolutionSavedComponent
  ]
})
export class SharedModule {
}
