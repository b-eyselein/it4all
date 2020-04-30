import {AdminIndexComponent} from './admin-index/admin-index.component';
import {AdminAuthGuard} from '../_helpers/admin-auth-guard';
import {CollectionToolAdminComponent} from './collection-tool-admin/collection-tool-admin.component';
import {AdminEditCollectionComponent} from './admin-edit-collection/admin-edit-collection.component';
import {CollectionAdminComponent} from './collection-admin/collection-admin.component';
import {AdminCollectionsIndexComponent} from './admin-collections-index/admin-collections-index.component';
import {AdminLessonsIndexComponent} from './admin-lessons-index/admin-lessons-index.component';
import {AdminEditExerciseComponent} from './admin-edit-exercise/admin-edit-exercise.component';

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';


const adminRoutes: Routes = [
  {
    path: 'admin',
    canActivate: [AdminAuthGuard],
    children: [
      {path: 'index', component: AdminIndexComponent},
      {
        path: ':toolId', children: [
          {path: '', component: CollectionToolAdminComponent},
          {
            path: 'collections', children: [
              {path: '', component: AdminCollectionsIndexComponent},
              {path: ':collId/editForm', component: AdminEditCollectionComponent},
              {path: ':collId/exercises', component: CollectionAdminComponent},
              {path: ':collId/exercises/:exId/editForm', component: AdminEditExerciseComponent},
            ]
          },
          {path: 'lessons', component: AdminLessonsIndexComponent},
        ]
      }
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
  CollectionAdminComponent,

  AdminEditCollectionComponent,


  AdminEditExerciseComponent,

  AdminLessonsIndexComponent
];
