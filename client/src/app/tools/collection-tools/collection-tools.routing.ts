import {CollectionToolOverviewComponent} from './collection-tool-overview/collection-tool-overview.component';
import {AuthGuard} from '../../_helpers/auth-guard';
import {CollectionOverviewComponent} from './collection-overview/collection-overview.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ExerciseOverviewComponent} from './exercise-overview/exercise-overview.component';
import {ExerciseComponent} from './exercise/exercise.component';

const collectionToolRoutes: Routes = [
  {
    path: '', canActivate: [AuthGuard],
    children: [
      {path: 'tools/:toolId', component: CollectionToolOverviewComponent},
      {path: 'tools/:toolId/collections/:collId', component: CollectionOverviewComponent},
      {path: 'tools/:toolId/collections/:collId/exercises/:exId', component: ExerciseOverviewComponent},
      {path: 'tools/:toolId/collections/:collId/exercises/:exId/parts/:partId', component: ExerciseComponent},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(collectionToolRoutes)],
  exports: [RouterModule]
})
export class CollectionToolRoutingModule {
}

export const collectionToolRoutingComponents = [
  CollectionToolOverviewComponent,
  CollectionOverviewComponent,
  ExerciseOverviewComponent,
  ExerciseComponent,
];
