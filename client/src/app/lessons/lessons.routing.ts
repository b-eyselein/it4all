import {LessonsForToolOverviewComponent} from './lessons-overview/lessons-for-tool-overview.component';
import {AuthGuard} from '../_helpers/auth-guard';
import {LessonComponent} from './lesson/lesson.component';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

const lessonsRoutes = [
  {path: ':toolId', component: LessonsForToolOverviewComponent, canActivate: [AuthGuard]},
  {path: ':toolId/lessons/:lessonId', component: LessonComponent, canActivate: [AuthGuard]},
];

@NgModule({
  imports: [RouterModule.forChild(lessonsRoutes)],
  exports: [RouterModule]
})
export class LessonsRoutingModule {
}

export const lessonsRoutingComponents = [
  LessonsForToolOverviewComponent,
  LessonComponent
];
