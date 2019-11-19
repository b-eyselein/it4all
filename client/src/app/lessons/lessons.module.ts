import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {lessonsRoutingComponents, LessonsRoutingModule} from './lessons.routing';
import {LessonTextContentComponent} from './lesson-text-content/lesson-text-content.component';
import {LessonApiService} from './_services/lesson-api.service';


@NgModule({
  declarations: [
    LessonTextContentComponent,

    ...lessonsRoutingComponents
  ],
  imports: [
    CommonModule,
    LessonsRoutingModule
  ],
  providers: [LessonApiService],
  exports: [
    LessonTextContentComponent
  ]
})
export class LessonsModule {
}
