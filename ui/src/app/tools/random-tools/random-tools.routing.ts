import {RandomOverviewComponent} from './random-overview/random-overview.component';
import {AuthGuard} from '../../_helpers/auth-guard';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {BoolExerciseComponent} from "./bool/bool-exercise/bool-exercise.component";
import {NaryExerciseComponent} from "./nary/nary-exercise/nary-exercise.component";

const randomToolRoutes: Routes = [
  {
    path: 'randomTools', canActivate: [AuthGuard], children: [
      {path: ':toolId', component: RandomOverviewComponent},
      {path: 'bool/:part', component: BoolExerciseComponent},
      {path: 'nary/:part', component: NaryExerciseComponent},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(randomToolRoutes)],
  exports: [RouterModule]
})
export class RandomToolsRoutingModule {
}


export const randomToolRoutingComponents = [
  RandomOverviewComponent,
  BoolExerciseComponent,
  NaryExerciseComponent,
];
