import {RandomOverviewComponent} from './random-overview/random-overview.component';
import {AuthGuard} from '../../_helpers/auth-guard';
import {BoolFillOutComponent} from './bool/bool-fill-out/bool-fill-out.component';
import {BoolCreateComponent} from './bool/bool-create/bool-create.component';
import {NaryAdditionComponent} from './nary/nary-addition/nary-addition.component';
import {NaryConversionComponent} from './nary/nary-conversion/nary-conversion.component';
import {NaryTwoConversionComponent} from './nary/nary-two-conversion/nary-two-conversion.component';
import {BoolDrawingComponent} from './bool/bool-drawing/bool-drawing.component';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

const randomToolRoutes = [
  {path: 'randomTools/:toolId', component: RandomOverviewComponent, canActivate: [AuthGuard]},

  {path: 'randomTools/bool/fillOut', component: BoolFillOutComponent, canActivate: [AuthGuard]},
  {path: 'randomTools/bool/create', component: BoolCreateComponent, canActivate: [AuthGuard]},
  {path: 'randomTools/bool/drawing', component: BoolDrawingComponent, canActivate: [AuthGuard]},

  {path: 'randomTools/nary/addition', component: NaryAdditionComponent, canActivate: [AuthGuard]},
  {path: 'randomTools/nary/conversion', component: NaryConversionComponent, canActivate: [AuthGuard]},
  {path: 'randomTools/nary/twoConversion', component: NaryTwoConversionComponent, canActivate: [AuthGuard]},
];

@NgModule({
  imports: [RouterModule.forChild(randomToolRoutes)],
  exports: [RouterModule]
})
export class RandomToolsRoutingModule {
}


export const randomToolRoutingComponents = [
  RandomOverviewComponent,
  BoolFillOutComponent, BoolCreateComponent, BoolDrawingComponent,
  NaryAdditionComponent, NaryConversionComponent, NaryTwoConversionComponent
];
