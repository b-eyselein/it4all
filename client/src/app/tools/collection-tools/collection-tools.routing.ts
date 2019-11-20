import {CollectionToolOverviewComponent} from './collection-tool-overview/collection-tool-overview.component';
import {AuthGuard} from '../../_helpers/auth-guard';
import {CollectionOverviewComponent} from './collection-overview/collection-overview.component';
import {ProgrammingExerciseComponent} from './programming/programming-exercise/programming-exercise.component';
import {RegexExerciseComponent} from './regex/regex-exercise/regex-exercise.component';
import {SqlExerciseComponent} from './sql/sql-exercise/sql-exercise.component';
import {WebExerciseComponent} from './web/web-exercise/web-exercise.component';
import {UmlExerciseComponent} from './uml/uml-exercise/uml-exercise.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ExerciseOverviewComponent} from './exercise-overview/exercise-overview.component';

const collectionToolRoutes: Routes = [
  {
    path: '', canActivate: [AuthGuard], children: [
      {path: 'tools/:toolId', component: CollectionToolOverviewComponent},
      {path: 'tools/:toolId/collections/:collId', component: CollectionOverviewComponent},
      {path: 'tools/:toolId/collections/:collId/exercises/:exId', component: ExerciseOverviewComponent},

      {path: 'tools/programming/collections/:collId/exercises/:exId/parts/:partId', component: ProgrammingExerciseComponent},
      {path: 'tools/regex/collections/:collId/exercises/:exId/parts/:partId', component: RegexExerciseComponent},
      {path: 'tools/sql/collections/:collId/exercises/:exId/parts/:partId', component: SqlExerciseComponent},
      {path: 'tools/uml/collections/:collId/exercises/:exId/parts/:partId', component: UmlExerciseComponent},
      {path: 'tools/web/collections/:collId/exercises/:exId/parts/:partId', component: WebExerciseComponent},
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
  ProgrammingExerciseComponent,
  RegexExerciseComponent,
  SqlExerciseComponent,
  UmlExerciseComponent,
  WebExerciseComponent
];
