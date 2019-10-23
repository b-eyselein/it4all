import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ToolOverviewComponent} from './tool-overview/tool-overview.component';
import {BoolFillOutComponent} from './tools/random-tools/bool/bool-fill-out/bool-fill-out.component';
import {ToolIndexComponent} from './tools/collection-tools/tool-index/tool-index.component';
import {CollectionIndexComponent} from './tools/collection-tools/collection-index/collection-index.component';
import {RegexExerciseComponent} from './tools/collection-tools/regex/regex-exercise/regex-exercise.component';
import {LoginFormComponent} from './user_management/login-form/login-form.component';
import {RandomOverviewComponent} from './tools/random-tools/random-overview/random-overview.component';
import {NaryAdditionComponent} from './tools/random-tools/nary/nary-addition/nary-addition.component';
import {NaryConversionComponent} from './tools/random-tools/nary/nary-conversion/nary-conversion.component';
import {NaryTwoConversionComponent} from './tools/random-tools/nary/nary-two-conversion/nary-two-conversion.component';
import {BoolCreateComponent} from './tools/random-tools/bool/bool-create/bool-create.component';
import {WebExerciseComponent} from './tools/collection-tools/web/web-exercise/web-exercise.component';
import {AuthGuard} from './_helpers/auth-guard';
import {ProgrammingExerciseComponent} from './tools/collection-tools/programming/programming-exercise/programming-exercise.component';
import {SqlExerciseComponent} from './tools/collection-tools/sql/sql-exercise/sql-exercise.component';
import {LtiComponent} from './lti/lti.component';
import {AdminIndexComponent} from './admin/admin-index/admin-index.component';
import {AdminAuthGuard} from './_helpers/admin-auth-guard';
import {CollToolAdminComponent} from './admin/coll-tool-admin/coll-tool-admin.component';
import {AdminReadCollectionsComponent} from './admin/admin-read-collections/admin-read-collections.component';

const routes: Routes = [
  {path: '', component: ToolOverviewComponent, canActivate: [AuthGuard]},

  {path: 'loginForm', component: LoginFormComponent},

  {path: 'lti/:uuid', component: LtiComponent},

  {path: 'admin', component: AdminIndexComponent},
  {path: 'admin/:toolId', component: CollToolAdminComponent, canActivate: [AdminAuthGuard]},
  {path: 'admin/:toolId/readCollections', component: AdminReadCollectionsComponent, canActivate: [AdminAuthGuard]},

  // Random  tools
  {path: 'randomTools/:toolId', component: RandomOverviewComponent, canActivate: [AuthGuard]},
  {path: 'randomTools/bool/fillOut', component: BoolFillOutComponent, canActivate: [AuthGuard]},
  {path: 'randomTools/bool/create', component: BoolCreateComponent, canActivate: [AuthGuard]},

  {path: 'randomTools/nary/addition', component: NaryAdditionComponent, canActivate: [AuthGuard]},
  {path: 'randomTools/nary/conversion', component: NaryConversionComponent, canActivate: [AuthGuard]},
  {path: 'randomTools/nary/twoConversion', component: NaryTwoConversionComponent, canActivate: [AuthGuard]},

  // Collection tools
  {path: 'tools/:toolId', component: ToolIndexComponent, canActivate: [AuthGuard]},
  {path: 'tools/:toolId/collections/:collId', component: CollectionIndexComponent, canActivate: [AuthGuard]},

  {
    path: 'tools/programming/collections/:collId/exercises/:exId/parts/:partId',
    component: ProgrammingExerciseComponent,
    canActivate: [AuthGuard],
  },
  {path: 'tools/regex/collections/:collId/exercises/:exId/parts/:partId', component: RegexExerciseComponent, canActivate: [AuthGuard]},
  {path: 'tools/sql/collections/:collId/exercises/:exId/parts/:partId', component: SqlExerciseComponent, canActivate: [AuthGuard]},
  {path: 'tools/web/collections/:collId/exercises/:exId/parts/:partId', component: WebExerciseComponent, canActivate: [AuthGuard]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
