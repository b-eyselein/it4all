import {ToolIndexComponent} from './tool-index/tool-index.component';
import {AuthGuard} from '../../_helpers/auth-guard';
import {CollectionIndexComponent} from './collection-index/collection-index.component';
import {ProgrammingExerciseComponent} from './programming/programming-exercise/programming-exercise.component';
import {RegexExerciseComponent} from './regex/regex-exercise/regex-exercise.component';
import {SqlExerciseComponent} from './sql/sql-exercise/sql-exercise.component';
import {WebExerciseComponent} from './web/web-exercise/web-exercise.component';

export const collectionToolRoutes = [
  {path: 'tools/:toolId', component: ToolIndexComponent, canActivate: [AuthGuard]},
  {path: 'tools/:toolId/collections/:collId', component: CollectionIndexComponent, canActivate: [AuthGuard]},

  {
    path: 'tools/programming/collections/:collId/exercises/:exId/parts/:partId',
    component: ProgrammingExerciseComponent,
    canActivate: [AuthGuard],
  },
  {path: 'tools/regex/collections/:collId/exercises/:exId/parts/:partId', component: RegexExerciseComponent, canActivate: [AuthGuard]},
  {path: 'tools/sql/collections/:collId/exercises/:exId/parts/:partId', component: SqlExerciseComponent, canActivate: [AuthGuard]},
  {path: 'tools/web/collections/:collId/exercises/:exId/parts/:partId', component: WebExerciseComponent, canActivate: [AuthGuard]},
];

export const collectionToolRoutingComponents = [
  ToolIndexComponent,
  CollectionIndexComponent,
  ProgrammingExerciseComponent,
  RegexExerciseComponent,
  SqlExerciseComponent,
  WebExerciseComponent
];
