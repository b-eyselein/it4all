import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {lessonsRoutingComponents, LessonsRoutingModule} from './lessons.routing';
import {LessonTextContentComponent} from './lesson-text-content/lesson-text-content.component';
import {LessonQuestionsContentComponent} from './_content-types/lesson-questions/lesson-questions-content.component';


@NgModule({
  declarations: [
    ...lessonsRoutingComponents,

    LessonTextContentComponent,
    LessonQuestionsContentComponent
  ],
  imports: [
    CommonModule,
    LessonsRoutingModule
  ],
  exports: [
    LessonTextContentComponent
  ]
})
export class LessonsModule {
}
