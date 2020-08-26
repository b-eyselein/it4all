import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TabComponent} from './tab/tab.component';
import {TabsComponent} from './tabs/tabs.component';
import {PointsNotificationComponent} from './points-notification/points-notification.component';
import {SolutionSavedComponent} from './solution-saved/solution-saved.component';
import {BreadcrumbsComponent} from "./breadcrumbs/breadcrumbs.component";
import {RouterModule} from "@angular/router";


@NgModule({
  declarations: [
    TabComponent,
    TabsComponent,
    PointsNotificationComponent,
    BreadcrumbsComponent,
    SolutionSavedComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [
    TabComponent,
    TabsComponent,
    PointsNotificationComponent,
    SolutionSavedComponent,
    BreadcrumbsComponent
  ]
})
export class SharedModule {
}
