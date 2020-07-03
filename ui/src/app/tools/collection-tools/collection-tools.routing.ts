import {NgModule} from '@angular/core';
import {CollectionToolOverviewComponent} from './collection-tool-overview/collection-tool-overview.component';
import {AuthGuard} from '../../_helpers/auth-guard';
import {CollectionOverviewComponent} from './collection-overview/collection-overview.component';
import {RouterModule, Routes} from '@angular/router';
import {ExerciseOverviewComponent} from './exercise-overview/exercise-overview.component';
import {ExerciseComponent} from './exercise/exercise.component';
import {AllExercisesOverviewComponent} from './all-exercises-overview/all-exercises-overview.component';
import {CollectionsListComponent} from './collections-list/collections-list.component';
import {LessonsForToolOverviewComponent} from './lessons/lessons-for-tool-overview/lessons-for-tool-overview.component';
import {LessonAsTextComponent} from './lessons/lesson-as-text/lesson-as-text.component';
import {LessonAsVideoComponent} from './lessons/lesson-as-video/lesson-as-video.component';
import {LessonOverviewComponent} from './lessons/lesson-overview/lesson-overview.component';

const collectionToolRoutes: Routes = [
  {
    path: 'tools/:toolId', canActivate: [AuthGuard], children: [
      {path: '', component: CollectionToolOverviewComponent},
      {
        path: 'lessons', children: [
          {path: '', component: LessonsForToolOverviewComponent},
          {
            path: ':lessonId', children: [
              {path: '', component: LessonOverviewComponent},
              {path: 'text', component: LessonAsTextComponent},
              {path: 'video', component: LessonAsVideoComponent}
            ]
          }
        ]
      },
      {
        path: 'collections', children: [
          {path: '', component: CollectionsListComponent},
          {
            path: ':collId', children: [
              {path: '', component: CollectionOverviewComponent},
              {path: 'exercises/:exId', component: ExerciseOverviewComponent},
              {path: 'exercises/:exId/parts/:partId', component: ExerciseComponent},
            ]
          },
        ]
      },
      {path: 'allExercises', component: AllExercisesOverviewComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(collectionToolRoutes)],
  exports: [RouterModule]
})
export class CollectionToolRoutingModule {
}

export const collectionToolRoutingComponents = [
  CollectionToolOverviewComponent,
  // Lesssons
  LessonsForToolOverviewComponent,
  LessonOverviewComponent,
  LessonAsTextComponent,
  LessonAsVideoComponent,
  // Collections
  CollectionsListComponent,
  CollectionOverviewComponent,
  // Exercises
  ExerciseOverviewComponent,
  ExerciseComponent,
  // AllExercises
  AllExercisesOverviewComponent,
];
