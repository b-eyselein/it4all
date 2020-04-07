import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {adminRoutingComponents, AdminRoutingModule} from './admin.routing';
import {AdminCollectionsIndexComponent} from './admin-collections-index/admin-collections-index.component';
import {AdminLessonsIndexComponent} from './admin-lessons-index/admin-lessons-index.component';
import {AdminReadLessonsComponent} from './admin-read-lessons/admin-read-lessons.component';
import {ReadObjectComponent} from './_components/read-object/read-object.component';
import { AdminEditExerciseComponent } from './admin-edit-exercise/admin-edit-exercise.component';


@NgModule({
  declarations: [
    ...adminRoutingComponents,

    AdminCollectionsIndexComponent,

    AdminLessonsIndexComponent,

    AdminReadLessonsComponent,

    ReadObjectComponent,

    AdminEditExerciseComponent
  ],
  imports: [
    CommonModule,
    AdminRoutingModule
  ]
})
export class AdminModule {
}
