import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ToolOverviewComponent} from './tool-overview/tool-overview.component';
import {LoginFormComponent} from './user_management/login-form/login-form.component';
import {AuthGuard} from './_helpers/auth-guard';
import {LtiComponent} from './lti/lti.component';
import {ToolTutorialsOverviewComponent} from './tutorials/tool-tutorials-overview/tool-tutorials-overview.component';
import {UmlTestComponent} from './tools/collection-tools/uml/uml-test/uml-test.component';
import {BoolDrawingComponent} from './tools/random-tools/bool/bool-drawing/bool-drawing.component';
import {randomToolRoutes, randomToolRoutingComponents} from './tools/random-tools/random-tools.routing';
import {adminRoutes, adminRoutingComponents} from './admin/admin.routing';
import {collectionToolRoutes, collectionToolRoutingComponents} from './tools/collection-tools/collection-tools.routing';

const routes: Routes = [
  {path: '', component: ToolOverviewComponent, canActivate: [AuthGuard]},

  {path: 'loginForm', component: LoginFormComponent},

  {path: 'lti/:uuid', component: LtiComponent},

  ...adminRoutes,

  // Tutorials
  {path: 'tutorials/:toolId', component: ToolTutorialsOverviewComponent, canActivate: [AuthGuard]},

  ...randomToolRoutes,
  ...collectionToolRoutes,

  // FIXME: remove this route...
  {path: 'test', component: UmlTestComponent},
  {path: 'boolDrawing', component: BoolDrawingComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

export const routingComponents = [
  ToolOverviewComponent,
  LoginFormComponent,
  LtiComponent,
  ...adminRoutingComponents,
  // tutorial routing
  ToolTutorialsOverviewComponent,
  ...randomToolRoutingComponents,
  ...collectionToolRoutingComponents,
  UmlTestComponent
];
