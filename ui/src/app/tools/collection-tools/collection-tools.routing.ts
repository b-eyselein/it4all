import {NgModule} from '@angular/core';
import {CollectionToolOverviewComponent} from './collection-tool-overview/collection-tool-overview.component';
import {AuthGuard} from '../../_helpers/auth-guard';
import {CollectionOverviewComponent} from './collection-overview/collection-overview.component';
import {RouterModule, Routes} from '@angular/router';
import {ExerciseOverviewComponent} from './exercise-overview/exercise-overview.component';
import {ExerciseComponent} from './exercise/exercise.component';
import {AllExercisesOverviewComponent} from './all-exercises-overview/all-exercises-overview.component';
import {CollectionsListComponent} from './collections-list/collections-list.component';

const collectionRoutes: Routes = [
  {path: '', component: CollectionOverviewComponent},
  {path: 'exercises/:exId', component: ExerciseOverviewComponent},
  {path: 'exercises/:exId/parts/:partId', component: ExerciseComponent},
];

const collectionsRoutes: Routes = [
  {path: '', component: CollectionsListComponent},
  {path: ':collId', children: collectionRoutes},
];


const collectionToolRoutes: Routes = [
  {
    path: '', canActivate: [AuthGuard],
    children: [
      {
        path: 'tools/:toolId', children: [
          {path: '', component: CollectionToolOverviewComponent},
          {path: 'allExercises', component: AllExercisesOverviewComponent},
          {path: 'collections', children: collectionsRoutes},
        ]
      },
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
  CollectionsListComponent,
  AllExercisesOverviewComponent,
  CollectionOverviewComponent,
  ExerciseOverviewComponent,
  ExerciseComponent,
];
