import {LessonsForToolOverviewComponent} from './lessons-overview/lessons-for-tool-overview.component';
import {AuthGuard} from '../_helpers/auth-guard';
import {LessonComponent} from './lesson/lesson.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const lessonsRoutes: Routes = [
  {
    path: '', canActivate: [AuthGuard], children: [
      {path: ':toolId', component: LessonsForToolOverviewComponent},
      {path: ':toolId/lessons/:lessonId', component: LessonComponent},
    ]
  }
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
