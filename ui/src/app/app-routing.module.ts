import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ToolOverviewComponent} from './tool-overview/tool-overview.component';
import {LoginFormComponent} from './user_management/login-form/login-form.component';
import {AuthGuard} from './_helpers/auth-guard';
import {LtiComponent} from './lti/lti.component';
import {RegisterFormComponent} from "./user_management/register-form/register-form.component";

const routes: Routes = [
  {path: '', component: ToolOverviewComponent, canActivate: [AuthGuard]},

  {path: 'registerForm', component: RegisterFormComponent},
  {path: 'loginForm', component: LoginFormComponent},

  {path: 'lti/:uuid', component: LtiComponent},

  {path: 'lessons', loadChildren: () => import('./lessons/lessons.module').then(m => m.LessonsModule)}
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
  RegisterFormComponent,
  LtiComponent,
];
