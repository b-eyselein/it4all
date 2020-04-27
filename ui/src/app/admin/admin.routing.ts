import {AdminIndexComponent} from './admin-index/admin-index.component';
import {AdminAuthGuard} from '../_helpers/admin-auth-guard';
import {CollectionToolAdminComponent} from './collection-tool-admin/collection-tool-admin.component';
import {AdminReadCollectionsComponent} from './admin-read-collections/admin-read-collections.component';
import {AdminEditCollectionComponent} from './admin-edit-collection/admin-edit-collection.component';
import {CollectionAdminComponent} from './collection-admin/collection-admin.component';
import {AdminReadExercisesComponent} from './admin-read-exercises/admin-read-exercises.component';
import {AdminCollectionsIndexComponent} from './admin-collections-index/admin-collections-index.component';
import {AdminLessonsIndexComponent} from './admin-lessons-index/admin-lessons-index.component';

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AdminReadLessonsComponent} from './admin-read-lessons/admin-read-lessons.component';
import {AdminEditExerciseComponent} from './admin-edit-exercise/admin-edit-exercise.component';

const adminCollectionRoutes: Routes = [
  {path: '', component: AdminCollectionsIndexComponent},
  {path: 'read', component: AdminReadCollectionsComponent},
  {path: ':collId/editForm', component: AdminEditCollectionComponent},

  {path: ':collId/exercises', component: CollectionAdminComponent},
  {path: ':collId/exercises/read', component: AdminReadExercisesComponent},

  {path: ':collId/exercises/:exId/editForm', component: AdminEditExerciseComponent},
];

const adminLessonRoutes: Routes = [
  {path: '', component: AdminLessonsIndexComponent},
  {path: 'read', component: AdminReadLessonsComponent}
]

const adminToolRoutes: Routes = [
  {path: '', component: CollectionToolAdminComponent},
  {path: 'collections', children: adminCollectionRoutes},
  {path: 'lessons', children: adminLessonRoutes}
]

const adminRoutes: Routes = [
  {
    path: 'admin',
    canActivate: [AdminAuthGuard],
    children: [
      {path: 'index', component: AdminIndexComponent},
      {path: ':toolId', children: adminToolRoutes}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(adminRoutes)],
  exports: [RouterModule]
})
export class AdminRoutingModule {
}

export const adminRoutingComponents = [
  AdminIndexComponent,

  CollectionToolAdminComponent,

  AdminCollectionsIndexComponent,
  AdminReadCollectionsComponent,
  AdminEditCollectionComponent,
  CollectionAdminComponent,
  AdminReadExercisesComponent,

  AdminLessonsIndexComponent
];
