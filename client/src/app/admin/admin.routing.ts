import {AdminIndexComponent} from './admin-index/admin-index.component';
import {AdminAuthGuard} from '../_helpers/admin-auth-guard';
import {CollectionToolAdminComponent} from './collection-tool-admin/collection-tool-admin.component';
import {AdminReadCollectionsComponent} from './admin-read-collections/admin-read-collections.component';
import {AdminEditCollectionComponent} from './admin-edit-collection/admin-edit-collection.component';
import {CollectionAdminComponent} from './collection-admin/collection-admin.component';
import {AdminReadExercisesComponent} from './admin-read-exercises/admin-read-exercises.component';
import {Routes} from '@angular/router';

export const adminRoutes: Routes = [
  {path: 'admin', component: AdminIndexComponent, canActivate: [AdminAuthGuard]},
  {path: 'admin/:toolId', component: CollectionToolAdminComponent, canActivate: [AdminAuthGuard]},
  {path: 'admin/:toolId/readCollections', component: AdminReadCollectionsComponent, canActivate: [AdminAuthGuard]},
  {path: 'admin/:toolId/collections/:collId/editForm', component: AdminEditCollectionComponent, canActivate: [AdminAuthGuard]},
  {path: 'admin/:toolId/collections/:collId/exercises', component: CollectionAdminComponent, canActivate: [AdminAuthGuard]},
  {path: 'admin/:toolId/collections/:collId/readExercises', component: AdminReadExercisesComponent, canActivate: [AdminAuthGuard]},
];

export const adminRoutingComponents = [
  AdminIndexComponent,
  CollectionToolAdminComponent,
  AdminReadCollectionsComponent, AdminEditCollectionComponent,
  CollectionAdminComponent,
  AdminReadExercisesComponent
];
