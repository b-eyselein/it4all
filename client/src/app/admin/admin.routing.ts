import {AdminIndexComponent} from './admin-index/admin-index.component';
import {AdminAuthGuard} from '../_helpers/admin-auth-guard';
import {CollectionToolAdminComponent} from './collection-tool-admin/collection-tool-admin.component';
import {AdminReadCollectionsComponent} from './admin-read-collections/admin-read-collections.component';
import {AdminEditCollectionComponent} from './admin-edit-collection/admin-edit-collection.component';
import {CollectionAdminComponent} from './collection-admin/collection-admin.component';
import {AdminReadExercisesComponent} from './admin-read-exercises/admin-read-exercises.component';
import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';

const adminRoutes: Routes = [
  {
    path: 'admin', canActivate: [AdminAuthGuard],
    children: [
      {path: '', component: AdminIndexComponent},
      {path: ':toolId', component: CollectionToolAdminComponent},
      {path: ':toolId/readCollections', component: AdminReadCollectionsComponent},
      {path: ':toolId/collections/:collId/editForm', component: AdminEditCollectionComponent},
      {path: ':toolId/collections/:collId/exercises', component: CollectionAdminComponent},
      {path: ':toolId/collections/:collId/readExercises', component: AdminReadExercisesComponent},
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
  AdminReadCollectionsComponent, AdminEditCollectionComponent,
  CollectionAdminComponent,
  AdminReadExercisesComponent
];
