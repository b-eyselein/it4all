import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {adminRoutingComponents, AdminRoutingModule} from './admin.routing';


@NgModule({
  declarations: adminRoutingComponents,
  imports: [
    CommonModule,
    AdminRoutingModule
  ]
})
export class AdminModule {
}
