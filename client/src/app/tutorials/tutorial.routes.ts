import {ToolTutorialsOverviewComponent} from './tool-tutorials-overview/tool-tutorials-overview.component';
import {AuthGuard} from '../_helpers/auth-guard';
import {LessonComponent} from './lesson/lesson.component';

export const tutorialRoutes = [
  {path: 'tutorials/:toolId', component: ToolTutorialsOverviewComponent, canActivate: [AuthGuard]},
  {path: 'tutorials/:toolId/lessons/:lessonId', component: LessonComponent, canActivate: [AuthGuard]},
];

export const tutorialRoutingComponents = [
  ToolTutorialsOverviewComponent,
  LessonComponent
];
