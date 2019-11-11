import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ToolOverviewComponent} from './tool-overview/tool-overview.component';
import {LoginFormComponent} from './user_management/login-form/login-form.component';
import {AuthGuard} from './_helpers/auth-guard';
import {LtiComponent} from './lti/lti.component';
import {UmlTestComponent} from './tools/collection-tools/uml/uml-test/uml-test.component';
import {BoolDrawingComponent} from './tools/random-tools/bool/bool-drawing/bool-drawing.component';
import {randomToolRoutes, randomToolRoutingComponents} from './tools/random-tools/random-tools.routing';
import {adminRoutes, adminRoutingComponents} from './admin/admin.routing';
import {collectionToolRoutes, collectionToolRoutingComponents} from './tools/collection-tools/collection-tools.routing';
import {tutorialRoutes, tutorialRoutingComponents} from './tutorials/tutorial.routes';

const routes: Routes = [
  {path: '', component: ToolOverviewComponent, canActivate: [AuthGuard]},

  {path: 'loginForm', component: LoginFormComponent},

  {path: 'lti/:uuid', component: LtiComponent},

  ...adminRoutes,

  ...tutorialRoutes,

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
  ...tutorialRoutingComponents,
  ...randomToolRoutingComponents,
  ...collectionToolRoutingComponents,
  UmlTestComponent
];
